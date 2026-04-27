package com.example.sentinal.domain.usecase.usage

import com.example.sentinal.data.local.entity.UsageEventEntity
import com.example.sentinal.domain.repository.UsageRepository
import javax.inject.Inject

class GetUsageEventsByPackageUseCase @Inject constructor(
    private val usageRepository: UsageRepository
) {
    suspend operator fun invoke( packageName: String, fromTimestamp: Long):List<UsageEventEntity>{
        return usageRepository.getUsageEventsByPackage(packageName,fromTimestamp)
    }
}
