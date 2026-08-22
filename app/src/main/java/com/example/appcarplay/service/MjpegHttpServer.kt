package com.example.appcarplay.service

import java.io.BufferedOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Servidor HTTP minimalista que expõe a tela do dispositivo como um stream MJPEG
 * (multipart/x-mixed-replace). Qualquer central multimídia com um navegador
 * consegue exibir o espelhamento acessando http://<ip>:<port>/ sem app dedicado.
 */
class MjpegHttpServer(private val port: Int = 8080) {

    private val boundary = "appcarplay-frame"
    private var serverSocket: ServerSocket? = null
    private val running = AtomicBoolean(false)
    private val pool = Executors.newCachedThreadPool()

    @Volatile private var latestFrame: ByteArray? = null
    private val frameLock = Object()

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
            socket.getInputStream().bufferedReader().readLine() // descarta a linha de request
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
        } catch (e: IOException) {
            // cliente desconectou
        } finally {
            try { socket.close() } catch (e: IOException) { /* noop */ }
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
}
