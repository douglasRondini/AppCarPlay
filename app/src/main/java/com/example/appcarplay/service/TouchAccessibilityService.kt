package com.example.appcarplay.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

/**
 * Simula toques reais na tela a partir dos eventos recebidos da central multimídia.
 * Precisa ser ativado manualmente pelo usuário em Configurações > Acessibilidade,
 * pois é a única forma sem root de injetar gestos de toque no Android.
 */
class TouchAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile var instance: TouchAccessibilityService? = null
            private set

        private const val TAP_DURATION_MS = 60L
        private const val MAX_SWIPE_DURATION_MS = 220L
        private const val TAP_SLOP_PX = 12f
    }

    private var pendingStartX = 0f
    private var pendingStartY = 0f
    private var pendingStartTime = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Não precisamos observar eventos de UI, só disparar gestos.
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        if (instance === this) instance = null
        super.onDestroy()
    }

    fun onTouchStart(x: Float, y: Float) {
        pendingStartX = x
        pendingStartY = y
        pendingStartTime = System.currentTimeMillis()
    }

    fun onTouchEnd(x: Float, y: Float) {
        val distance = Math.hypot((x - pendingStartX).toDouble(), (y - pendingStartY).toDouble())
        val elapsed = (System.currentTimeMillis() - pendingStartTime).coerceIn(1, MAX_SWIPE_DURATION_MS)

        if (distance <= TAP_SLOP_PX) {
            dispatchTap(pendingStartX, pendingStartY)
        } else {
            dispatchSwipe(pendingStartX, pendingStartY, x, y, elapsed)
        }
    }

    private fun dispatchTap(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, TAP_DURATION_MS)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }

    private fun dispatchSwipe(x1: Float, y1: Float, x2: Float, y2: Float, durationMs: Long) {
        val path = Path().apply {
            moveTo(x1, y1)
            lineTo(x2, y2)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }
}
