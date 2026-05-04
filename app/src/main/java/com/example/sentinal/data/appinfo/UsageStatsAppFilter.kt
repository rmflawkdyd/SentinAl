package com.example.sentinal.data.appinfo

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UsageStatsAppFilter @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun shouldExclude(packageName: String): Boolean {
        return packageName == context.packageName ||
            packageName in excludedSamsungLauncherPackages ||
            packageName in homePackageNames
    }

    private val homePackageNames: Set<String> by lazy {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }

        queryIntentActivities(homeIntent)
            .mapNotNull { it.activityInfo?.packageName }
            .toSet()
    }

    private fun queryIntentActivities(intent: Intent): List<ResolveInfo> {
        val packageManager = context.packageManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
    }

    private companion object {
        val excludedSamsungLauncherPackages = setOf(
            "com.sec.android.app.launcher",
            "com.samsung.android.app.launcher",
        )
    }
}
