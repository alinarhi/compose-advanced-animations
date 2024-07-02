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
import ru.edu.hse.advanced_animations.shaders.FRACTAL_SHADER_SRC

fun Modifier.fractalBLineShader(
    speed: Float = 0.6f,
    spread: Float = 0.6f,
    iterations: Float = 3f,
    zoom: Float = 0.25f,
    offset: Float = -1f
): Modifier = composed {

    val shader = remember { RuntimeShader(FRACTAL_SHADER_SRC) }

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
                "size",
                size.width.toFloat(),
                size.height.toFloat()
            )
        }
        .graphicsLayer {
            clip = true
            shader.setFloatUniform("time", timeMs.floatValue * speed)
            shader.setFloatUniform("spread", spread)
            shader.setFloatUniform("iterations_", iterations)
            shader.setFloatUniform("zoom", zoom)
            shader.setFloatUniform("offset", offset)
            shader.setFloatUniform("iChannel", iChannelValues)

            renderEffect =
                RenderEffect
                    .createRuntimeShaderEffect(shader, "composable")
                    .asComposeRenderEffect()
        }
}

private val iChannelValues = floatArrayOf(
    0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f,
    0.2f, 0.3f, 0.4f, 0.1f, 0.2f, 0.3f
)

