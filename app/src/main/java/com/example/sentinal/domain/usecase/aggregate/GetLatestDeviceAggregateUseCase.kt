package com.example.sentinal.domain.usecase.aggregate

import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import com.example.sentinal.domain.repository.DeviceAggregateRepository
import javax.inject.Inject

class GetLatestDeviceAggregateUseCase @Inject constructor(
    private val deviceAggregateRepository: DeviceAggregateRepository
) {
    suspend operator fun invoke(): DeviceAggregate5MinEntity?{
        return deviceAggregateRepository.getLatestAggregate()
    }
}
