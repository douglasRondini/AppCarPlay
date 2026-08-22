package com.example.appcarplay.service

import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Servidor HTTP minimalista que expõe a tela do dispositivo como um stream MJPEG
 * (multipart/x-mixed-replace) e recebe eventos de toque da central multimídia,
 * repassando-os para o [TouchAccessibilityService]. Qualquer navegador consegue
 * exibir e controlar o espelhamento acessando http://<ip>:<port>/ sem app dedicado.
 */
class MjpegHttpServer(private val port: Int = 8080) {

    private val boundary = "appcarplay-frame"
    private var serverSocket: ServerSocket? = null
    private val running = AtomicBoolean(false)
    private val pool = Executors.newCachedThreadPool()

    @Volatile private var latestFrame: ByteArray? = null
    private val frameLock = Object()

    /** Recebe (tipo, fracaoX 0..1, fracaoY 0..1) de eventos de toque vindos do navegador. */
    var onTouchEvent: ((type: String, xFrac: Double, yFrac: Double) -> Unit)? = null

    fun start() {
        if (running.getAndSet(true)) return
        pool.execute {
            try {
                serverSocket = ServerSocket(port)
                while (running.get()) {
                    val client = serverSocket?.accept() ?: break
                    pool.execute { handleClient(client) }
                }
            } catch (e: IOException) {
                // socket fechado ao parar o servidor
            }
        }
    }

    fun stop() {
        running.set(false)
        try { serverSocket?.close() } catch (e: IOException) { /* noop */ }
        pool.shutdownNow()
    }

    fun pushFrame(jpegBytes: ByteArray) {
        synchronized(frameLock) {
            latestFrame = jpegBytes
            frameLock.notifyAll()
        }
    }

    private fun handleClient(socket: Socket) {
        try {
            val reader = socket.getInputStream().bufferedReader()
            val requestLine = reader.readLine() ?: return
            val path = requestLine.split(" ").getOrNull(1) ?: "/"
            consumeHeaders(reader)

            when {
                path.startsWith("/stream") -> serveStream(socket)
                path.startsWith("/touch") -> serveTouch(socket, path)
                else -> serveViewerPage(socket)
            }
        } catch (e: IOException) {
            // cliente desconectou
        } finally {
            try { socket.close() } catch (e: IOException) { /* noop */ }
        }
    }

    private fun consumeHeaders(reader: BufferedReader) {
        while (true) {
            val line = reader.readLine() ?: break
            if (line.isEmpty()) break
        }
    }

    private fun serveViewerPage(socket: Socket) {
        val out = BufferedOutputStream(socket.getOutputStream())
        val body = VIEWER_HTML.toByteArray(StandardCharsets.UTF_8)
        val header = "HTTP/1.0 200 OK\r\n" +
            "Content-Type: text/html; charset=utf-8\r\n" +
            "Content-Length: ${body.size}\r\n" +
            "Connection: close\r\n\r\n"
        out.write(header.toByteArray())
        out.write(body)
        out.flush()
    }

    private fun serveTouch(socket: Socket, path: String) {
        val query = path.substringAfter('?', "")
        val params = query.split('&').mapNotNull {
            val idx = it.indexOf('=')
            if (idx < 0) null else {
                val key = it.substring(0, idx)
                val value = URLDecoder.decode(it.substring(idx + 1), "UTF-8")
                key to value
            }
        }.toMap()

        val type = params["type"]
        val x = params["x"]?.toDoubleOrNull()
        val y = params["y"]?.toDoubleOrNull()

        if (type != null && x != null && y != null) {
            onTouchEvent?.invoke(type, x.coerceIn(0.0, 1.0), y.coerceIn(0.0, 1.0))
        }

        val out = BufferedOutputStream(socket.getOutputStream())
        val header = "HTTP/1.0 204 No Content\r\nConnection: close\r\n\r\n"
        out.write(header.toByteArray())
        out.flush()
    }

    private fun serveStream(socket: Socket) {
        val out = BufferedOutputStream(socket.getOutputStream())
        val header = "HTTP/1.0 200 OK\r\n" +
            "Server: AppCarPlay-Mirror\r\n" +
            "Connection: close\r\n" +
            "Cache-Control: no-cache\r\n" +
            "Content-Type: multipart/x-mixed-replace; boundary=$boundary\r\n\r\n"
        out.write(header.toByteArray())
        out.flush()

        while (running.get() && !socket.isClosed) {
            val frame = waitForNextFrame() ?: break
            val partHeader = "--$boundary\r\nContent-Type: image/jpeg\r\nContent-Length: ${frame.size}\r\n\r\n"
            out.write(partHeader.toByteArray())
            out.write(frame)
            out.write("\r\n".toByteArray())
            out.flush()
        }
    }

    private fun waitForNextFrame(): ByteArray? {
        synchronized(frameLock) {
            val start = latestFrame
            var waited = 0
            while (latestFrame === start && running.get() && waited < 5000) {
                frameLock.wait(200)
                waited += 200
            }
            return latestFrame
        }
    }

    companion object {
        // Página simples: mostra o stream MJPEG e envia toques (início/fim) como
        // frações da imagem, para o servidor traduzir em coordenadas reais da tela.
        private val VIEWER_HTML = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1"/>
              <title>AppCarPlay - Espelhamento</title>
              <style>
                html, body { margin:0; padding:0; background:#000; height:100%; overflow:hidden; }
                img { width:100%; height:100%; object-fit:contain; display:block; touch-action:none; user-select:none; }
              </style>
            </head>
            <body>
              <img id="mirror" src="/stream" draggable="false"/>
              <script>
                var img = document.getElementById('mirror');
                function send(type, evt) {
                  var rect = img.getBoundingClientRect();
                  var xFrac = (evt.clientX - rect.left) / rect.width;
                  var yFrac = (evt.clientY - rect.top) / rect.height;
                  if (xFrac < 0 || xFrac > 1 || yFrac < 0 || yFrac > 1) return;
                  fetch('/touch?type=' + type + '&x=' + xFrac.toFixed(4) + '&y=' + yFrac.toFixed(4));
                }
                img.addEventListener('pointerdown', function(e) { send('start', e); });
                img.addEventListener('pointerup', function(e) { send('end', e); });
                img.addEventListener('contextmenu', function(e) { e.preventDefault(); });
              </script>
            </body>
            </html>
        """.trimIndent()
    }
}
