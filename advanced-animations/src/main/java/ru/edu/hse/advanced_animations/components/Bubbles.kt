package ru.edu.hse.advanced_animations.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import kotlinx.coroutines.delay
import ru.edu.hse.advanced_animations.shaders.BUBBLES_SHADER


fun Modifier.bubblesShader(
    direction: Float = 1f,
): Modifier = composed {

    val shader = remember { RuntimeShader(BUBBLES_SHADER) }

    val timeMs = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            timeMs.floatValue = (System.currentTimeMillis() % 100_000L) / 1_000f
            delay(10)
        }
    }

    this
        .onSizeChanged { size ->
            shader.setFloatUniform(
                "iResolution",
                size.width.toFloat(),
                size.height.toFloat()
            )
        }
        .graphicsLayer {
            clip = true
            shader.setFloatUniform("iTime", timeMs.floatValue)
            shader.setFloatUniform("dir", direction)

            renderEffect =
                RenderEffect
                    .createRuntimeShaderEffect(shader, "composable")
                    .asComposeRenderEffect()
        }
}

