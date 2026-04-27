package com.example.sentinal.di

import com.example.sentinal.domain.repository.AppUsageDailyRepository
import com.example.sentinal.data.repository.AppUsageDailyRepositoryImpl
import com.example.sentinal.data.repository.DeviceAggregateRepositoryImpl
import com.example.sentinal.data.repository.MemorySnapshotRepositoryImpl
import com.example.sentinal.data.repository.UsageRepositoryImpl
import com.example.sentinal.domain.repository.DeviceAggregateRepository
import com.example.sentinal.domain.repository.MemorySnapshotRepository
import com.example.sentinal.domain.repository.UsageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUsageRepository(
        impl: UsageRepositoryImpl
    ): UsageRepository

    @Binds
    @Singleton
    abstract fun bindMemorySnapshotRepository(
        impl: MemorySnapshotRepositoryImpl
    ): MemorySnapshotRepository

    @Binds
    @Singleton
    abstract fun bindDeviceAggregateRepository(
        impl: DeviceAggregateRepositoryImpl
    ): DeviceAggregateRepository

    @Binds
    @Singleton
    abstract fun bindAppUsageDailyRepository(
        impl: AppUsageDailyRepositoryImpl
    ): AppUsageDailyRepository


}
