package com.miquido.stringstranslator.model.parsing

data class SingleStringJsonModel(val key: String = "", val value: String = "")

data class PluralStringJsonModel(val key: String = "", val pluralsMap: PluralMap = PluralMap())

class PluralMap : HashMap<String, String>()
