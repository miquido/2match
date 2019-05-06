package com.miquido.stringstranslator.stringwriter

import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.TranslationModel

interface StringWriter {

    fun writePluralStringsDataToFile(
            translations: MutableMap<LanguageCode, MutableList<PluralTranslationModel>>?,
            output: String
    )

    fun writeSingleStringsDataToFile(
            translations: MutableMap<LanguageCode, MutableList<TranslationModel>>?,
            output: String
    )
}