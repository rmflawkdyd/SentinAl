package com.example.sentinal.data.collector

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.sentinal.data.appinfo.UsageStatsAppFilter
import com.example.sentinal.data.collector.model.UsageEventRaw
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UsageStatsCollector @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val usageStatsAppFilter: UsageStatsAppFilter,
){
    fun collect(fromTimestamp:Long, toTimestamp:Long):List<UsageEventRaw>{
        val usageStateManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val usageEvents = usageStateManager.queryEvents(fromTimestamp, toTimestamp)
        val event = UsageEvents.Event()
        val results = mutableListOf<UsageEventRaw>()

        while (usageEvents.hasNextEvent()){
            usageEvents.getNextEvent(event)

            val packageName = event.packageName?:continue
            if (usageStatsAppFilter.shouldExclude(packageName)) continue

            results.add(
                UsageEventRaw(
                    timestamp = event.timeStamp,
                    packageName = packageName,
                    className = event.className,
                    eventType = event.eventType
                )
            )
        }
        return  results
    }
}
