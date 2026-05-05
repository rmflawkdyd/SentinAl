package com.example.sentinal.presentation.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sentinal.ui.theme.SentinAIBg
import com.example.sentinal.ui.theme.SentinAICardColor
import com.example.sentinal.ui.theme.SentinAIChartInactive
import com.example.sentinal.ui.theme.SentinAIInfo
import com.example.sentinal.ui.theme.SentinAIInk
import com.example.sentinal.ui.theme.SentinAILine
import com.example.sentinal.ui.theme.SentinAIMuted
import com.example.sentinal.ui.theme.SentinAINavy
import com.example.sentinal.ui.theme.SentinAISecondaryText
import com.example.sentinal.ui.theme.SentinAITransparent
import com.example.sentinal.ui.theme.SentinAIWhite
import com.example.sentinal.ui.theme.SentinAITextStyles

@Composable
fun ScreenSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SentinAIBg),
    ) {
        content()
    }
}

@Composable
fun SentinCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(SentinAICardColor)
            .border(1.dp, SentinAIWhite, shape),
    ) {
        content()
    }
}

@Composable
fun StateMessage(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = SentinAINavy)
            Spacer(modifier = Modifier.height(18.dp))
        }
        Text(
            text = title,
            color = SentinAIInk,
            style = SentinAITextStyles.SectionTitle,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),
            color = SentinAIMuted,
            style = SentinAITextStyles.BodySmallRelaxed,
            textAlign = TextAlign.Center,
        )
        if (buttonText != null && onButtonClick != null) {
            Button(
                onClick = onButtonClick,
                modifier = Modifier.padding(top = 18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SentinAINavy),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun HeaderBlock(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = SentinAIInk,
            style = SentinAITextStyles.ScreenTitle,
        )
        Text(
            text = subtitle,
            color = SentinAISecondaryText,
            style = SentinAITextStyles.BodySmall,
        )
    }
}

@Composable
fun ScoreRing(
    score: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(224.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(192.dp)) {
            val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(SentinAILine, style = stroke)
            drawArc(
                color = SentinAINavy,
                startAngle = -90f,
                sweepAngle = 360f * (score.coerceIn(0, 100) / 100f),
                useCenter = false,
                style = stroke,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                color = SentinAIInk,
                style = SentinAITextStyles.MetricLarge,
            )
            Text(
                text = "/100",
                color = SentinAIMuted,
                style = SentinAITextStyles.Label.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Composable
fun BarChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    highlightIndex: Int? = null,
    activeColor: Color = SentinAINavy,
    inactiveColor: Color = SentinAIChartInactive,
    cornerRadius: Float = 8f,
    hideZeroValues: Boolean = false,
) {
    val maxValue = values.maxOrNull()?.takeIf { it > 0f } ?: 1f
    Row(
        modifier = modifier.height(136.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        values.forEachIndexed { index, value ->
            val ratio = (value / maxValue).coerceIn(0.12f, 1f)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(ratio)
                    .clip(RoundedCornerShape(topStart = cornerRadius.dp, topEnd = cornerRadius.dp))
                    .background(
                        if (hideZeroValues && value <= 0f) {
                            SentinAITransparent
                        } else if (highlightIndex == index) {
                            activeColor
                        } else {
                            inactiveColor
                        },
                    ),
            )
        }
    }
}

@Composable
fun SegmentedUsageBar(
    segments: List<Pair<Float, Color>>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(16.dp)
            .clip(RoundedCornerShape(999.dp)),
    ) {
        segments.forEach { (weight, color) ->
            Box(
                modifier = Modifier
                    .weight(weight.coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(color),
            )
        }
    }
}

@Composable
fun MetricGlyph(kind: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(18.dp)) {
        val color = SentinAIInfo
        val stroke = Stroke(width = 1.8f, cap = StrokeCap.Round)
        when (kind) {
            "memory" -> {
                drawRoundRect(color, Offset(4f, 4f), Size(size.width - 8f, size.height - 8f), CornerRadius(2f), stroke)
                repeat(3) { i ->
                    val p = 2f + i * 5f
                    drawLine(color, Offset(p, 1f), Offset(p, 4f), strokeWidth = 1.6f)
                    drawLine(color, Offset(p, size.height - 4f), Offset(p, size.height - 1f), strokeWidth = 1.6f)
                    drawLine(color, Offset(1f, p), Offset(4f, p), strokeWidth = 1.6f)
                    drawLine(color, Offset(size.width - 4f, p), Offset(size.width - 1f, p), strokeWidth = 1.6f)
                }
            }
            "spark" -> {
                repeat(3) { x ->
                    repeat(3) { y ->
                        drawCircle(color, radius = 1.7f, center = Offset(4f + x * 5f, 4f + y * 5f))
                    }
                }
            }
            else -> {
                drawCircle(color, radius = size.minDimension * 0.35f, style = stroke)
                drawLine(color, Offset(size.width / 2f, size.height * 0.82f), Offset(size.width / 2f, size.height), strokeWidth = 1.8f)
            }
        }
    }
}

@Composable
fun BottomInputFade(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to SentinAITransparent,
                    0.45f to SentinAIBg,
                    1f to SentinAIBg,
                ),
            )
            //.padding(start = 0.dp, end = 0.dp, top = 16.dp, bottom = 8.dp)
    ) {
        content()
    }
}


@Composable
fun Dot(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
fun LegendDot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color),
    )
}
