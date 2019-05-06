package com.miquido.stringstranslator.model.translations

typealias LanguageCode = String

typealias PlatformKey = String

sealed class TranslationModel(val key: String, val value: String)

class AndroidTranslationModel(
        key: String,
        value: String,
        val isTranslatable: Boolean = true,
        val isFormatted: Boolean = true
) : TranslationModel(key, value)

class IosTranslationModel(key: String, value: String) : TranslationModel(key, value)

class WebTranslationModel(key: String, value: String) : TranslationModel(key, value)

data class SingleStringsTranslations(
        val platforms: MutableMap<PlatformKey, MutableMap<LanguageCode, MutableList<TranslationModel>>>
)

data class PluralStringsTranslations(
        val platforms: MutableMap<PlatformKey, MutableMap<LanguageCode, MutableList<PluralTranslationModel>>>
)

enum class PluralQualifier {
    ZERO,
    ONE,
    TWO,
    FEW,
    MANY,
    OTHER;

    override fun toString() = super.toString().toLowerCase()
}

class PluralTranslationModel(val key: String, val pluralsMap: Map<PluralQualifier, String>)