package com.leandrolcd.doganalyzer.ui.auth

import android.graphics.Paint
import androidx.annotation.ColorRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import com.leandrolcd.doganalyzer.ui.model.Values
import kotlin.math.cos
import kotlin.math.sin

@ExperimentalTextApi
@Composable
fun SemiCircularGauge(
    values: Values,
    modifier: Modifier,
    strokeIndicator: Float = 20f,
    @ColorRes textColor: Color = Color.Black,
    @ColorRes indicatorColor: Color = Color.Green,
    @ColorRes linesColor: Color = Color.Black
) {
    val sweep = remember { Animatable(0f) }
    val valueRange = values.max - values.min
    val currentValue = (values.current - values.min).coerceIn(0f, valueRange) / valueRange
    val textMeasurer = rememberTextMeasurer()
    LaunchedEffect(currentValue) {
        sweep.animateTo(currentValue * 200, animationSpec = tween(1000))
    }
    Canvas(modifier = modifier) {
        val halfStrokeWidth = strokeIndicator / 2
        val radius = size.minDimension / 2 - halfStrokeWidth
        val center = Offset(size.width / 2, size.height / 2)
        drawArc(
            color = linesColor,
            startAngle = 170f,
            sweepAngle = 200f,
            useCenter = false,
            style = Stroke(width = strokeIndicator + 5)
        )

        // Dibujar el semicírculo de fondo
        drawArc(
            color = Color.LightGray,
            startAngle = 171f,
            sweepAngle = 198f,
            useCenter = false,
            style = Stroke(width = strokeIndicator)
        )
        val numLines = 5
        val angle = 200f / numLines
        for (i in 0..numLines) {
            val lineAngle = 170f + angle * i
            val start = polarToCartesian(radius, lineAngle, center)
            val end = polarToCartesian(radius - 20f, lineAngle, center)
            drawLine(linesColor, start, end, strokeWidth = 3f)
        }

        // Dibujar el semicírculo indicador
        drawArc(
            color = indicatorColor,
            startAngle = 171f,
            sweepAngle = sweep.value - 1,
            useCenter = false,
            style = Stroke(width = strokeIndicator)
        )


        val bottom = Offset((size.width / 2f) - 100f, size.height)


        val textPaint = Paint().apply {
            textSize = 50f
            color = textColor.toArgb()
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val unitPaint = Paint().apply {
            textSize = 25f
            color = textColor.toArgb()
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val text = "${values.current.toInt()}"

        // Obtener el ancho del texto
        val textHeight = textMeasurer.measure(AnnotatedString(text)).size.height
        // Dibujar el texto y la línea de referencia
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                text,
                center.x,
                (bottom.y / 2),
                textPaint
            )
        }
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                values.unit,
                center.x,
                (bottom.y / 2) + textHeight,
                unitPaint
            )
        }

    }
}

private fun polarToCartesian(radius: Float, angle: Float, center: Offset): Offset {
    val radians = Math.toRadians(angle.toDouble())
    val x = (radius * cos(radians)).toFloat() + center.x
    val y = (radius * sin(radians)).toFloat() + center.y
    return Offset(x, y)
}
