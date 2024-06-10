package com.island.iot

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch


val FAKE_REPOSITORY = StateRepository(
    {},
    RemoteDataSourceFake(),
    LocalDataSourceFake(),
    MemoryDataSourceFake(),
    ArduinoDataSourceFake()
)

class StateViewModel(application: Application) : AndroidViewModel(application) {
    val repository = StateRepository(
        { viewModelScope.launch(block = it) },
        RemoteDataSourceImpl(),
        LocalDataSourceImpl(application),
        MemoryDataSourceImpl(),
        ArduinoDataSourceImpl()
    )
}
