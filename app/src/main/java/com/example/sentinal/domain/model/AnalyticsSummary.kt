package com.example.sentinal.domain.model

data class AnalyticsSummary(
    val totalUsageMillis: Long,
    val changeRatePercent: Float,
    val hasPreviousUsageData: Boolean,
    val nightUsageRatePercent: Float,
    val dailyUsages:List<DailyUsagePoint>,
    val categoryUsages: List<CategoryUsagePoint>,
    val topApps: List<AppUsagePoint>,
)

data class DailyUsagePoint(
    val dateEpoch: Long,
    val usageMillis: Long,
)

data class CategoryUsagePoint(
    val category: String,
    val usageMillis: Long,
)

data class AppUsagePoint(
    val packageName: String,
    val appName: String?,
    val usageMillis: Long,
    val launchCount: Int,
)
