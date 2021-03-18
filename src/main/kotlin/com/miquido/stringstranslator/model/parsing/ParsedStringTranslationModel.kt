package com.miquido.stringstranslator.model.parsing

import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.TranslationModel

data class ParsedStringTranslationModel(
        val singleStringSet: SingleStringSetModel = SingleStringSetModel(linkedMapOf()),
        val pluralStringSet: PluralStringSetModel = PluralStringSetModel(linkedMapOf())
)

data class SingleStringSetModel(
        val singleString: LinkedHashMap<LanguageCode, SingleStringValuesModel> = linkedMapOf()
)

data class SingleStringValuesModel(
        val singleStringValue: LinkedHashMap<String, TranslationModel> = linkedMapOf()
)

data class PluralStringSetModel(
        val pluralString: LinkedHashMap<LanguageCode, PluralStringValuesModel> = linkedMapOf()
)

data class PluralStringValuesModel(
        val pluralStringValue: LinkedHashMap<String, PluralTranslationModel> = linkedMapOf()
)
