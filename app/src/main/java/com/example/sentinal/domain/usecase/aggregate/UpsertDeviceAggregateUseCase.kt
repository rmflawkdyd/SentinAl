package com.example.sentinal.domain.usecase.aggregate

import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import com.example.sentinal.domain.repository.DeviceAggregateRepository
import javax.inject.Inject

class UpsertDeviceAggregateUseCase @Inject constructor(
    private val deviceAggregateRepository: DeviceAggregateRepository
) {
    suspend operator fun invoke(entity: DeviceAggregate5MinEntity){
        deviceAggregateRepository.upsertAggregate(entity)
    }
}
