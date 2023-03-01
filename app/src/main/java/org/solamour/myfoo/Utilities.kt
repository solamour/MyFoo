package org.solamour.myfoo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun <T> MutableStateFlow<MutableList<T>>.add(item: T) {
    update {
        value.toMutableList().apply {
            add(item)
        }
    }
}
