package com.example.sentinal.data.local.datasource

import com.example.sentinal.data.local.dao.DeviceAggregateDao
import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceAggregateLocalDataSource @Inject constructor(
    private val deviceAggregateDao: DeviceAggregateDao
) {

    suspend fun upsertAggregate(entity: DeviceAggregate5MinEntity){
        deviceAggregateDao.upsertAggregate(entity)
    }

    fun observeAggregates(fromWindowStart: Long): Flow<List<DeviceAggregate5MinEntity>>{
        return deviceAggregateDao.observeAggregates(fromWindowStart)
    }

    suspend fun getLatestAggregate(): DeviceAggregate5MinEntity?{
        return deviceAggregateDao.getLatestAggregate()
    }

}