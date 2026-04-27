package com.example.sentinal.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sentinal.data.local.dao.AppUsageDailyDao
import com.example.sentinal.data.local.dao.DeviceAggregateDao
import com.example.sentinal.data.local.dao.MemorySnapshotDao
import com.example.sentinal.data.local.dao.UsageEventDao
import com.example.sentinal.data.local.entity.AppUsageDailyEntity
import com.example.sentinal.data.local.entity.DeviceAggregate5MinEntity
import com.example.sentinal.data.local.entity.MemorySnapshotEntity
import com.example.sentinal.data.local.entity.UsageEventEntity

@Database(
    entities = [
        UsageEventEntity::class,
        MemorySnapshotEntity::class,
        DeviceAggregate5MinEntity::class,
        AppUsageDailyEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class SentinAlDatabase: RoomDatabase() {
    abstract fun usageEventDao(): UsageEventDao
    abstract fun memorySnapshotDao(): MemorySnapshotDao
    abstract fun deviceAggregateDao(): DeviceAggregateDao
    abstract fun appUsageDailyDao(): AppUsageDailyDao
}