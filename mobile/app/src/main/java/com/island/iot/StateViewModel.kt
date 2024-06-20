package com.island.iot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


val FAKE_REPOSITORY = StateRepository(
    {},
    RemoteDataSourceFake(),
    LocalDataSourceFake(),
    MemoryDataSourceFake(),
    ArduinoDataSourceFake(),
    NewsDataSourceImpl()
)

class StateViewModel(application: Application) : AndroidViewModel(application) {
    val repository = StateRepository(
        { viewModelScope.launch(block = it) },
        RemoteDataSourceImpl(),
        LocalDataSourceImpl(application),
        MemoryDataSourceImpl(),
        ArduinoDataSourceImpl(),
        NewsDataSourceImpl()
    )
}
