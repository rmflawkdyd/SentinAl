package com.example.sentinal.domain.model

data class ChatContext(
    val guardianResult: GuardianResult?,
    val analyticsSummary: AnalyticsSummary,
) {
    val topCategory: CategoryUsagePoint?
        get() = analyticsSummary.categoryUsages.maxByOrNull { it.usageMillis }
}
