package com.example.sentinal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = SentinAITextStyles.Body.copy(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp,
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

object SentinAITextStyles {
    val MetricLarge = TextStyle(
        fontSize = 40.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight.Light,
    )

    val ScreenTitle = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val HeroTitle = TextStyle(
        fontSize = 24.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val AppTitle = TextStyle(
        fontSize = 22.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold,
    )

    val SectionTitle = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val InsightTitle = TextStyle(
        fontSize = 18.sp,
        lineHeight = 27.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val Body = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
    )

    val BodyRelaxed = TextStyle(
        fontSize = 16.sp,
        lineHeight = 26.sp,
    )

    val BodyMedium = TextStyle(
        fontSize = 15.sp,
        lineHeight = 22.sp,
    )

    val BodySmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )

    val BodySmallRelaxed = TextStyle(
        fontSize = 14.sp,
        lineHeight = 22.sp,
    )

    val Label = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    val Caption = TextStyle(
        fontSize = 12.sp,
        lineHeight = 18.sp,
    )

    val SmallLabel = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
    )

    val Tiny = TextStyle(
        fontSize = 10.sp,
        lineHeight = 15.sp,
    )

    val Overline = Label.copy(
        letterSpacing = 1.2.sp,
    )
}
