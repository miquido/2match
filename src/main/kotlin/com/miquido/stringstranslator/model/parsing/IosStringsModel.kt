package com.miquido.stringstranslator.model.parsing

import com.miquido.stringstranslator.model.translations.LanguageCode

data class XcStrings(
    val sourceLanguage: String,
    val strings: Map<String, StringLocalization>,
    val version: String
)

data class StringLocalization(
    val localizations: Map<LanguageCode, Localization>
)

data class Localization(
    val stringUnit: StringUnit? = null,
    val substitutions: Map<String, Substitution>? = null,
    val variations: Variations? = null
)

data class StringUnit(
    val state: String,
    val value: String
)

data class Substitution(
    val argNum: Int,
    val formatSpecifier: String,
    val variations: Variations
)

data class Variations(
    val plural: Map<String, Localization>
)
