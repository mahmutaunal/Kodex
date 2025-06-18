package com.mahmutalperenunal.kodex.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromQrContentType(value: QrContentType): String = value.name

    @TypeConverter
    fun toQrContentType(value: String): QrContentType = QrContentType.valueOf(value)
}