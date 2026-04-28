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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SentinBg = Color(0xFFF7FAFC)
val SentinInk = Color(0xFF041632)
val SentinNavy = Color(0xFF1B2B48)
val SentinText = Color(0xFF181C1E)
val SentinMuted = Color(0xFF75777E)
val SentinSubtle = Color(0xFF94A3B8)
val SentinCard = Color.White
val SentinLine = Color(0xFFF1F5F9)
val SentinPanel = Color(0xFFF1F4F6)
val SentinAccent = Color(0xFFC3E8FA)
val SentinMint = Color(0xFF9FF1E6)
val SentinGood = Color(0xFF059669)
val SentinDanger = Color(0xFFEF4444)

@Composable
fun ScreenSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SentinBg),
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
            .background(SentinCard)
            .border(1.dp, Color.White, shape),
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
            CircularProgressIndicator(color = SentinNavy)
            Spacer(modifier = Modifier.height(18.dp))
        }
        Text(
            text = title,
            color = SentinInk,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),
            color = SentinMuted,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
        )
        if (buttonText != null && onButtonClick != null) {
            Button(
                onClick = onButtonClick,
                modifier = Modifier.padding(top = 18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SentinNavy),
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
            color = SentinInk,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = subtitle,
            color = Color(0xFF44474D),
            fontSize = 14.sp,
            lineHeight = 20.sp,
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
            drawCircle(SentinLine, style = stroke)
            drawArc(
                color = SentinNavy,
                startAngle = -90f,
                sweepAngle = 360f * (score.coerceIn(0, 100) / 100f),
                useCenter = false,
                style = stroke,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                color = SentinInk,
                fontSize = 40.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Light,
            )
            Text(
                text = "/100",
                color = SentinMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun BarChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    highlightIndex: Int? = null,
    activeColor: Color = SentinNavy,
    inactiveColor: Color = Color(0xFFE8EDF3),
    cornerRadius: Float = 8f,
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
                    .background(if (highlightIndex == index) activeColor else inactiveColor),
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
        val color = Color(0xFF466978)
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
                    0f to Color.Transparent,
                    0.45f to SentinBg,
                    1f to SentinBg,
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
