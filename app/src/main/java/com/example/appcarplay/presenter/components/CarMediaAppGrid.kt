package com.example.appcarplay.presenter.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appcarplay.domain.model.AppItem
import com.example.appcarplay.ui.theme.consoleAccent
import com.example.appcarplay.ui.theme.consoleBorder
import com.example.appcarplay.ui.theme.consoleSurface
import com.example.appcarplay.ui.theme.textSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CarMediaAppGrid(
    apps: List<AppItem>,
    onRemoveApp: (AppItem) -> Unit,
    onAddApp: () -> Unit
) {
    val context = LocalContext.current

    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(apps, key = { _, app -> app.pack }) { index, app ->
            StaggeredEntrance(index = index) {
                Box {
                    DashboardButton(app.name, app.icon, app.accent) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.url))
                        intent.setPackage(app.pack)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(app.url)
                            )
                            context.startActivity(webIntent)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(consoleSurface)
                            .clickable { onRemoveApp(app) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remover ${app.name}",
                            tint = textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier
                    .width(132.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(consoleSurface)
                    .clickable(onClick = onAddApp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(consoleBorder.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Adicionar app",
                                tint = consoleAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Adicionar",
                            fontSize = 12.sp,
                            color = textSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Anima a entrada de cada card com um pequeno atraso proporcional à posição no grid,
 * criando um efeito de "cascata" quando os apps selecionados mudam.
 */
@Composable
private fun StaggeredEntrance(index: Int, content: @Composable () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(28f) }

    LaunchedEffect(Unit) {
        delay((index * 45L).coerceAtMost(360L))
        launch { alpha.animateTo(1f, tween(320, easing = FastOutSlowInEasing)) }
        launch { offsetY.animateTo(0f, tween(380, easing = FastOutSlowInEasing)) }
    }

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha.value
            translationY = offsetY.value
        }
    ) {
        content()
    }
}
