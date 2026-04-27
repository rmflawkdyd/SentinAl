package com.example.sentinal.domain.usecase.collector

import com.example.sentinal.data.collector.UsageStatsCollector
import com.example.sentinal.data.collector.mapper.UsageEventMapper
import com.example.sentinal.domain.repository.UsageRepository
import javax.inject.Inject

class CollectUsageEventsUseCase @Inject constructor(
    private val usageStatsCollector: UsageStatsCollector,
    private val usageEventMapper: UsageEventMapper,
    private val usageRepository: UsageRepository,
) {
    suspend operator fun invoke(fromTimestamp: Long, toTimestamp: Long) {
        val rawEvents = usageStatsCollector.collect(
            fromTimestamp = fromTimestamp,
            toTimestamp = toTimestamp
        )
        val entities = usageEventMapper.mapList(rawEvents)
        usageRepository.insertUsageEvents(entities)
    }
}