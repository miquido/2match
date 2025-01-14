package com.miquido.stringstranslator.stringwriter

import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.TranslationModel

interface StringWriter {
    fun write(
        singleStrings: Map<LanguageCode, List<TranslationModel>>?,
        pluralStrings: Map<LanguageCode, List<PluralTranslationModel>>?,
        output: String
    )
}
