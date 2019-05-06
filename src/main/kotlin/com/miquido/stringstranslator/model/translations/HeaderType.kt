package com.miquido.stringstranslator.model.translations

sealed class HeaderType

class SingleType(val language: LanguageCode, val cellIndex: Int) : HeaderType()

class ListType(val list: List<String>) : HeaderType()
