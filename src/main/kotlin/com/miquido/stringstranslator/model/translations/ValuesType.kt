package com.miquido.stringstranslator.model.translations

sealed class ValuesType

class ListValuesType(val list: List<String>) : ValuesType()

class MapValuesType(val map: Map<PluralQualifier, String>) : ValuesType()

class SingleValueType(val item: String) : ValuesType()
