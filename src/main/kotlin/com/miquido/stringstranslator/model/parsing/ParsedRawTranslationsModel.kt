package com.miquido.stringstranslator.model.parsing

data class ParsedRawTranslationsModel(
        val stringsSingle: List<List<String>> = listOf(),
        val stringsPlural: List<List<String>> = listOf()
)