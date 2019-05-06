package com.miquido.stringstranslator.model.parsing

import java.io.File

class IosSingleStringsModel : HashMap<String, String>() {

    fun load(file: File) {
        file.forEachLine {
            if (it.isNotBlank() && !it.startsWith(COMMENT_PREFIX)) {
                val keyValue = it.split(KEY_VALUE_DELIMITER).map { value -> value.trim() }
                //removes first and last quotation mark
                val key = keyValue[0].substring(1, keyValue[0].length - 1)
                //removes first quotation mark and last quotation mark and semicolon
                val value = keyValue[1].substring(1, keyValue[1].length - 2)
                put(key, value)
            }
        }
    }

    companion object {
        private const val COMMENT_PREFIX = "//"
        private const val KEY_VALUE_DELIMITER = "="
    }
}