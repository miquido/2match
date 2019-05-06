package com.miquido.stringstranslator.extensions

/**
 * Replaces substrings according to provided map
 */
fun String.escape(escapeMap: Map<String, String>): String {
    var escapedString = this
    escapeMap.forEach { escapedString = escapedString.replace(it.key, it.value) }
    return escapedString
}