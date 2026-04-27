package com.example.sentinal.data.appinfo

import android.content.Context
import android.content.pm.ApplicationInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppCategoryResolver @Inject constructor(
    @param:ApplicationContext private val contexct: Context
) {
    fun resolveCategory(packageName: String): String {
        val packageManager = contexct.packageManager

        val applicationInfo = runCatching {
            packageManager.getApplicationInfo(packageName, 0)
        }.getOrNull()?:return CATEGORY_UNKNOWN
        return when (applicationInfo.category) {
            ApplicationInfo.CATEGORY_GAME -> "게임"
            ApplicationInfo.CATEGORY_AUDIO -> "오디오"
            ApplicationInfo.CATEGORY_VIDEO -> "비디오"
            ApplicationInfo.CATEGORY_IMAGE -> "이미지"
            ApplicationInfo.CATEGORY_SOCIAL -> "소셜"
            ApplicationInfo.CATEGORY_NEWS -> "뉴스"
            ApplicationInfo.CATEGORY_MAPS -> "지도"
            ApplicationInfo.CATEGORY_PRODUCTIVITY -> "생산성"
            else -> CATEGORY_UNKNOWN
        }
    }

    private companion object {
        const val CATEGORY_UNKNOWN = "기타"
    }
}
