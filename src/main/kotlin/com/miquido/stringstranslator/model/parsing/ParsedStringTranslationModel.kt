package com.miquido.stringstranslator.model.parsing

import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.TranslationModel

data class ParsedStringTranslationModel(
        val singleStringSet: SingleStringSetModel = SingleStringSetModel(hashMapOf()),
        val pluralStringSet: PluralStringSetModel = PluralStringSetModel(hashMapOf())
)

data class SingleStringSetModel(
        val singleString: HashMap<LanguageCode, SingleStringValuesModel> = hashMapOf()
)

data class SingleStringValuesModel(
        val singleStringValue: HashMap<String, TranslationModel> = hashMapOf()
)

data class PluralStringSetModel(
        val pluralString: HashMap<LanguageCode, PluralStringValuesModel> = hashMapOf()
)

data class PluralStringValuesModel(
        val pluralStringValue: HashMap<String, PluralTranslationModel> = hashMapOf()
)
