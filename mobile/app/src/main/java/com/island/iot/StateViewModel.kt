package com.island.iot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.launch

class StateViewModel(application: Application) : AndroidViewModel(application) {
    val repository = StateRepository(
        Room.databaseBuilder(
            application,
            AppDatabase::class.java, "database"
        ).build()
    )

    init {
        viewModelScope.launch { repository.updateJugs() }
    }
}