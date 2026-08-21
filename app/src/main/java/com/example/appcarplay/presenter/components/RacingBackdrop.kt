package com.example.appcarplay.presenter.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Camada decorativa de fundo, bem discreta: uma silhueta de carro de corrida derrapando
 * no canto da tela, com marcas de pneu e fumaça subindo devagar. Tudo em opacidade muito
 * baixa (sombra, não imagem) para não competir com os cards por cima.
 */
@Composable
fun RacingBackdrop(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "racing_backdrop")

    val smokeDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = LinearEasing)
        ),
        label = "smoke_drift"
    )

    val silhouettePulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "silhouette_pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Sombra do carro: canto inferior direito, quase fora da tela.
        val carCenter = Offset(w * 0.88f, h * 0.80f)
        val carRadius = w * 0.36f * silhouettePulse
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent),
                center = carCenter,
                radius = carRadius
            ),
            radius = carRadius,
            center = carCenter
        )

        // Marcas de borrachão no asfalto.
        val tireMark = Color.White.copy(alpha = 0.045f)
        drawLine(
            color = tireMark,
            start = Offset(w * 0.42f, h * 0.94f),
            end = Offset(w * 0.98f, h * 0.80f),
            strokeWidth = 16f
        )
        drawLine(
            color = tireMark,
            start = Offset(w * 0.48f, h * 1.0f),
            end = Offset(w * 1.02f, h * 0.86f),
            strokeWidth = 11f
        )

        // Fumaça subindo em camadas, cada uma numa fase diferente do ciclo.
        repeat(5) { i ->
            val phase = (smokeDrift + i * 0.2f) % 1f
            val cx = w * (0.74f + i * 0.035f)
            val cy = h * (0.86f - phase * 0.5f)
            val radius = (64f + i * 16f) * (0.6f + phase * 0.6f)
            val alpha = (0.06f * (1f - phase)).coerceIn(0f, 0.06f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = alpha), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = radius
                ),
                radius = radius,
                center = Offset(cx, cy)
            )
        }
    }
}
