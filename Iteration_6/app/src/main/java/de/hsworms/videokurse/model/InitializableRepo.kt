package de.hsworms.videokurse.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class InitializableRepo {

    private val _initialized = MutableLiveData(false)

    val isInitialized: LiveData<Boolean>
        get() {return _initialized}

    private fun setInitialized() {
        _initialized.value = true
    }

    protected abstract suspend fun _initialize()

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            _initialize()
        }
        setInitialized()
    }

}