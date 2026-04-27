package com.example.sentinal.data.local.datasource

import com.example.sentinal.data.local.dao.UsageEventDao
import com.example.sentinal.data.local.entity.UsageEventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UsageEventLocalDataSource @Inject constructor(
    private val usageEventDao: UsageEventDao
){
    suspend fun insert(events:List<UsageEventEntity>){
        usageEventDao.insertUsageEvents(events)
    }

    fun observeRecent(fromTimestamp: Long): Flow<List<UsageEventEntity>>{
        return usageEventDao.observeUsageEvents(fromTimestamp)
    }

    suspend fun getUsageEventByPackage(packageName: String, fromTimestamp: Long):List<UsageEventEntity>{
        return usageEventDao.getUsageEventByPackage(packageName,fromTimestamp)
    }

}