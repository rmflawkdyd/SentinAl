package com.example.sentinal.domain.usecase.analytics

import com.example.sentinal.data.local.entity.AppUsageDailyEntity
import com.example.sentinal.domain.repository.AppUsageDailyRepository
import javax.inject.Inject

class UpsertAppUsageDailyUseCase @Inject constructor(
    private val appUsageDailyRepository: AppUsageDailyRepository
) {
    suspend operator fun invoke(items:List<AppUsageDailyEntity>){
        return appUsageDailyRepository.upsertDailyUsage(items)
    }
}
