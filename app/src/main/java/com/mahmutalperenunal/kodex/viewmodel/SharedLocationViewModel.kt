package com.mahmutalperenunal.kodex.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedLocationViewModel : ViewModel() {
    private val _latitude = MutableStateFlow<String?>(null)
    private val _longitude = MutableStateFlow<String?>(null)
    private val _address = MutableStateFlow<String?>(null)

    val latitude: StateFlow<String?> = _latitude
    val longitude: StateFlow<String?> = _longitude
    val address: StateFlow<String?> = _address

    fun setLocation(lat: Double, lon: Double, address: String?) {
        _latitude.value = lat.toString()
        _longitude.value = lon.toString()
        _address.value = address
    }
}