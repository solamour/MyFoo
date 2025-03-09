package org.solamour.myfoo

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.thedeanda.lorem.LoremIpsum
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class MyFooViewModel(
    private val application: Application,
) : ViewModel() {
    private val _logList = mutableStateListOf<LogItem>()
    val logList: List<LogItem> = _logList

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                MyFooViewModel(application)
            }
        }

        private val TAG = MyFooViewModel::class.qualifiedName
    }

    init {
    }

    fun onAction(action: Action) {
        when (action) {
            Action.Play -> log(LoremIpsum.getInstance().firstNameFemale)
            Action.ClearLog -> _logList.clear()
        }
    }

    fun log(string: String = "") {
        val timestamp = DateTimeFormatter.ofPattern("mm:ss.SSS").format(LocalDateTime.now())
        val log = if (string.isEmpty()) string else "[$timestamp] $string"
        _logList.add(LogItem(log = log))
        Log.d(TAG, log)
    }
}

//--------------------------------------------------------------------------------------------------
sealed interface Action {
    data object Play : Action
    data object ClearLog : Action
}

//----------------------------------------------------------------------------------------------
data class LogItem(
    val key: String = UUID.randomUUID().toString(),
    val log: String = "",
)
