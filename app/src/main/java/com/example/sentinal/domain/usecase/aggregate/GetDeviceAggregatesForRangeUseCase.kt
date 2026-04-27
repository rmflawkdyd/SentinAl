package com.example.sentinal.domain.usecase.aggregate

import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import com.example.sentinal.domain.repository.DeviceAggregateRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetDeviceAggregatesForRangeUseCase @Inject constructor(
    private val deviceAggregateRepository: DeviceAggregateRepository
){
    suspend operator fun invoke(fromWindowStart: Long):List<DeviceAggregate5MinEntity>{
        return deviceAggregateRepository.observeAggregates(fromWindowStart).first()
    }
}