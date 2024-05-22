package com.island.iot

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class State(
    val signedIn: Boolean = false,
)

class StateViewModel(application: Application) : AndroidViewModel(application) {
    //    val repository = StateRepository(
//        Room.databaseBuilder(
//            application,
//            AppDatabase::class.java, "database"
//        ).build()
//    )
    val repository = StateRepository(application.getSharedPreferences("data", Context.MODE_PRIVATE))

    fun register(username: String, password: String) {
        viewModelScope.launch {
            repository.register(username, password)
        }
    }

}