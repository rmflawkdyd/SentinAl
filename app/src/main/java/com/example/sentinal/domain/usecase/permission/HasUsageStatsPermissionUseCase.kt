package com.example.sentinal.domain.usecase.permission

import com.example.sentinal.data.permission.UsageStatsPermissionChecker
import javax.inject.Inject

class HasUsageStatsPermissionUseCase @Inject constructor(
    private val usageStatsPermissionChecker: UsageStatsPermissionChecker
) {
    operator fun invoke(): Boolean{
       return usageStatsPermissionChecker.hasPermission()
    }
}