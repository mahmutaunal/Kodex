package com.mahmutalperenunal.kodex.data

import android.content.Context
import androidx.room.*

@Database(entities = [QrEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qrDao(): QrDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "qr_db").build()
                    .also { INSTANCE = it }
            }
        }
    }
}