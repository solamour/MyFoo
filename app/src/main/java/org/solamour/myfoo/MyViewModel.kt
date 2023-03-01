package org.solamour.myfoo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyViewModel : ViewModel() {
    private val _log = MutableStateFlow(mutableListOf<String>())
    val log = _log.asStateFlow()

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                MyViewModel()
            }
        }

        private val TAG = MyViewModel::class.qualifiedName
    }

    fun log(string: String) {
        _log.add(string)
        Log.d(TAG, "$string\n")
    }
}
