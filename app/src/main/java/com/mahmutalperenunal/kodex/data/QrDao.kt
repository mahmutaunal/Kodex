package com.mahmutalperenunal.kodex.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QrDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qr: QrEntity)

    @Delete
    suspend fun delete(qr: QrEntity)

    @Query("SELECT * FROM qr_table ORDER BY timestamp DESC")
    fun getAll(): Flow<List<QrEntity>>
}