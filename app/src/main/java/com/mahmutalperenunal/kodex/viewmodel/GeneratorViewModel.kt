package com.mahmutalperenunal.kodex.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mahmutalperenunal.kodex.data.QrContentType

class GeneratorViewModel : ViewModel() {
    var selectedContentType by mutableStateOf(QrContentType.TEXT)
    var inputFields = mutableStateMapOf<String, String>()
    var qrBitmap by mutableStateOf<Bitmap?>(null)
    var encryptEnabled by mutableStateOf(false)
    var errorFields = mutableStateOf(setOf<String>())
}