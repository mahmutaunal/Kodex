package com.mahmutalperenunal.kodex.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_table")
data class QrEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val type: QrType, // SCANNED or GENERATED
    val timestamp: Long = System.currentTimeMillis()
)

enum class QrType { SCANNED, GENERATED }