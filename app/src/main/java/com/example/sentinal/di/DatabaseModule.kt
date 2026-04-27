package com.example.sentinal.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sentinal.data.local.db.SentinAlDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideSentinAlDatabase(
        @ApplicationContext context: Context): SentinAlDatabase {
        return  Room.databaseBuilder(
            context,
            SentinAlDatabase::class.java,
            "sentinal.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideUsageEventDao(db: SentinAlDatabase) = db.usageEventDao()

    @Provides
    fun provideMemorySnapshotDao(
        db: SentinAlDatabase
    ) = db.memorySnapshotDao()

    @Provides
    fun provideDeviceAggregateDao(db: SentinAlDatabase) = db.deviceAggregateDao()

    @Provides
    fun provideAppUsageDailyDao(db: SentinAlDatabase) = db.appUsageDailyDao()

    private val MIGRATION_1_2 = object : Migration(1,2){
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                ALTER TABLE app_usage_daily
                ADD COLUMN nightUsageMillis INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
            )
        }
    }
}