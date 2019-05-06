package com.miquido.stringstranslator.functions

fun <K> mapOfNonEmptyStrings(vararg pairs: Pair<K, String>): Map<K, String> {
    val map = mutableMapOf<K, String>()
    pairs.forEach {
        if (it.second.isNotBlank()) map[it.first] = it.second
    }
    return map
}