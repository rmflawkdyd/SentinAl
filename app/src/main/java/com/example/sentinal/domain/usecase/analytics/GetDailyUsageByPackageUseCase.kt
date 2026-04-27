package com.example.sentinal.domain.usecase.analytics

import com.example.sentinal.data.local.entity.AppUsageDailyEntity
import com.example.sentinal.domain.repository.AppUsageDailyRepository
import javax.inject.Inject

class GetDailyUsageByPackageUseCase @Inject constructor(
    private val appUsageDailyRepository: AppUsageDailyRepository
) {
    suspend operator fun invoke(packageName: String, fromDay: Long, toDay: Long, ): List<AppUsageDailyEntity> {
        return appUsageDailyRepository.getDailyUsageByPackage(packageName,fromDay,toDay)

    }
}
