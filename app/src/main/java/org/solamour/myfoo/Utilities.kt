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

@OptIn(ExperimentalUnsignedTypes::class)
fun ByteArray.toHexString() = asUByteArray().joinToString("") {
    it.toString(16).padStart(2, '0')
}
