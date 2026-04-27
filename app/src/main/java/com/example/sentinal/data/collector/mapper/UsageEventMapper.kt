package com.example.sentinal.data.collector.mapper

import com.example.sentinal.data.collector.model.UsageEventRaw
import com.example.sentinal.data.local.entity.UsageEventEntity
import javax.inject.Inject

class UsageEventMapper @Inject constructor() {

    fun map(raw: UsageEventRaw): UsageEventEntity {
        return UsageEventEntity(
            timestamp = raw.timestamp,
            packageName = raw.packageName,
            className = raw.className,
            eventType = raw.eventType,
            createdAt = System.currentTimeMillis(),
        )
    }

    fun mapList(rawItems:List<UsageEventRaw>):List<UsageEventEntity>{
        return rawItems.map { map(it) }
    }

}