package com.example.sentinal.domain.usecase.aggregate

import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import com.example.sentinal.domain.repository.DeviceAggregateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDeviceAggregatesUseCase @Inject constructor(
    private val deviceAggregateRepository: DeviceAggregateRepository
) {
    operator fun invoke(fromWindowStart: Long): Flow<List<DeviceAggregate5MinEntity>>{
        return deviceAggregateRepository.observeAggregates(fromWindowStart)
    }

}
