package com.digitaltorque.structed.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.App
import com.digitaltorque.structed.config.AppConfig
import com.digitaltorque.structed.utils.FileUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import logic.Field
import viewmodels.ViewerViewModel

class BrowserViewModel(
    application: Application,
    @Suppress("unused") private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private var goViewModel: ViewerViewModel? = null
        set(value) {
            field = value
            _state.value = field?.cloneState()
            value?.let {
                it.observe("browser") { state ->
                    _state.value = state
                }
            }
        }
    var file: Uri? = null
        set(value) {
            field = value
            value?.let {
                val data = FileUtil.readFile(getApplication<Application>().applicationContext, it)
                goViewModel = ViewerViewModel(data)
            }
        }

    val waitDuration: Long
        get() {
//            return App.config().getInt(AppConfig.ASK_SUPPORT_WAIT)
            return 0
        }

    private val _state = MutableStateFlow(goViewModel?.cloneState())
    val state = _state.asStateFlow()

    fun getField(path: String): Field? {
        return goViewModel?.getPath(path)
    }

    fun setField(parentPath: String, field: Field) {
        goViewModel?.setPath(parentPath, field)
    }

    fun getFormat(): Int {
        return (goViewModel?.format ?: 0).toInt()
    }

    fun setFormat(format: Int) {
        goViewModel?.format = format.toLong()
    }

    fun fileData(): ByteArray {
        return goViewModel?.fileData() ?: ByteArray(0)
    }

    fun clearError() {
        val newState = _state.value
        newState?.error = null
        goViewModel?.updateState(newState)
    }

    fun setError(error: Exception) {
        val newState = _state.value
        newState?.error = error
        goViewModel?.updateState(newState)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val application = this[APPLICATION_KEY] as Application
                BrowserViewModel(application, savedStateHandle)
            }
        }
    }
}
