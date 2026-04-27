package com.example.sentinal.data.repository

import com.example.sentinal.data.local.datasource.DeviceAggregateLocalDataSource
import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import com.example.sentinal.domain.repository.DeviceAggregateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceAggregateRepositoryImpl @Inject constructor(
    private val deviceAggregateLocalDataSource: DeviceAggregateLocalDataSource
): DeviceAggregateRepository {
    override suspend fun upsertAggregate(entity: DeviceAggregate5MinEntity) {
        deviceAggregateLocalDataSource.upsertAggregate(entity)
    }

    override fun observeAggregates(fromWindowStart: Long): Flow<List<DeviceAggregate5MinEntity>> {
        return deviceAggregateLocalDataSource.observeAggregates(fromWindowStart)
    }

    override suspend fun getLatestAggregate(): DeviceAggregate5MinEntity? {
        return deviceAggregateLocalDataSource.getLatestAggregate()
    }
}
