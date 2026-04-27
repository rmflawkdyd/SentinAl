package com.example.sentinal.domain.usecase.usage

import com.example.sentinal.data.local.entity.UsageEventEntity
import com.example.sentinal.domain.repository.UsageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRecentUsageEventsUseCase @Inject constructor(
    private val usageRepository: UsageRepository
) {
    operator fun invoke(fromTimestamp: Long): Flow<List<UsageEventEntity>>{
        return usageRepository.observeRecentUsageEvents(fromTimestamp)
    }
}
