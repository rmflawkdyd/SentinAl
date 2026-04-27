package com.example.sentinal.data.collector

import android.app.ActivityManager
import android.content.Context
import com.example.sentinal.data.collector.model.MemorySnapshotRaw
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MemoryInfoCollector @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun collect(timestamp:Long = System.currentTimeMillis()): MemorySnapshotRaw {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        return MemorySnapshotRaw(
            timestamp = timestamp,
            availableMemBytes = memoryInfo.availMem,
            totalMemBytes = memoryInfo.totalMem,
            lowMemory = memoryInfo.lowMemory
        )
    }
}
