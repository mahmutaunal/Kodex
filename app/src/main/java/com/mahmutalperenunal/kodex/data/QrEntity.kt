package com.mahmutalperenunal.kodex.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mahmutalperenunal.kodex.R

@Entity(tableName = "qr_table")
data class QrEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val type: QrType, // SCANNED or GENERATED
    val contentType: QrContentType = QrContentType.TEXT,
    val timestamp: Long = System.currentTimeMillis()
)

enum class QrType { SCANNED, GENERATED }

enum class QrContentType {
    TEXT, URL, EMAIL, PHONE, SMS, WIFI, GEO, VCARD, EVENT, ENCRYPTED;

    fun getLabelRes(): Int {
        return when (this) {
            TEXT -> R.string.qr_type_text
            URL -> R.string.qr_type_url
            EMAIL -> R.string.qr_type_email
            PHONE -> R.string.qr_type_phone
            SMS -> R.string.qr_type_sms
            WIFI -> R.string.qr_type_wifi
            GEO -> R.string.qr_type_geo
            VCARD -> R.string.qr_type_vcard
            EVENT -> R.string.qr_type_event
            ENCRYPTED -> R.string.qr_type_encrypted
        }
    }
}