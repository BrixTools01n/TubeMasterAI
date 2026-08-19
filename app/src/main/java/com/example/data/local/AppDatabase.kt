package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedItemEntity::class,
        HistoryEntity::class,
        UserEntity::class,
        PaymentEntity::class,
        ToolOverrideEntity::class,
        NotificationEntity::class,
        AdminAuditLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedDao(): SavedDao
    abstract fun historyDao(): HistoryDao
    abstract fun userDao(): UserDao
    abstract fun paymentDao(): PaymentDao
    abstract fun toolOverrideDao(): ToolOverrideDao
    abstract fun notificationDao(): NotificationDao
    abstract fun adminAuditLogDao(): AdminAuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tubemaster_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
