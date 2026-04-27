package com.example.sentinal.domain.usecase.analytics

import com.example.sentinal.data.local.entity.AppUsageDailyEntity
import com.example.sentinal.domain.repository.AppUsageDailyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDailyUsageRangeUseCase @Inject constructor(
    private val appUsageDailyRepository: AppUsageDailyRepository
) {
    operator fun invoke(fromDay: Long, toDay: Long) : Flow<List<AppUsageDailyEntity>> {
        return appUsageDailyRepository.observeDailyUsageRange(fromDay,toDay)
    }
}
