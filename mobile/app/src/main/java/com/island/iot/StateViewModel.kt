package com.island.iot

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.launch

class StateViewModel(application: Application) : AndroidViewModel(application) {
    val _repository = StateRepository(
        Room.databaseBuilder(
            application,
            AppDatabase::class.java, "database"
        ).build()
    )

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _repository.register(username, password)
            Log.d("hcjshfjdhfdf","djjsjhfdjhfjhdj")
        }
    }

}