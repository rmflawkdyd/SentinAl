package com.example.sentinal.domain.repository

import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import kotlinx.coroutines.flow.Flow

interface DeviceAggregateRepository {
    suspend fun upsertAggregate(entity: DeviceAggregate5MinEntity)
    fun observeAggregates(fromWindowStart: Long): Flow<List<DeviceAggregate5MinEntity>>
    suspend fun getLatestAggregate(): DeviceAggregate5MinEntity?
}