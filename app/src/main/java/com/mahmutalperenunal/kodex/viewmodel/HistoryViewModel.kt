package com.mahmutalperenunal.kodex.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mahmutalperenunal.kodex.data.AppDatabase
import com.mahmutalperenunal.kodex.data.QrEntity
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).qrDao()
    val qrHistory: LiveData<List<QrEntity>> = dao.getAll().asLiveData()

    fun delete(qr: QrEntity) = viewModelScope.launch {
        dao.delete(qr)
    }

    fun insert(qr: QrEntity) = viewModelScope.launch {
        dao.insert(qr)
    }
}