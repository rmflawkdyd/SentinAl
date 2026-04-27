package com.example.sentinal.data.appinfo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppLabelResolver @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun resolveAppName(packageName: String): String{
        val packageManager = context.packageManager

        val applicationInfo = runCatching {
            packageManager.getApplicationInfo(packageName,0)
        }.getOrNull()?:return packageName

        return runCatching {
            packageManager.getApplicationLabel(applicationInfo).toString()
        }.getOrElse {
            packageName
        }
    }
}