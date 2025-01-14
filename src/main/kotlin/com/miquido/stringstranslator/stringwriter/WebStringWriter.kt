package com.miquido.stringstranslator.stringwriter

import com.google.gson.Gson
import com.miquido.stringstranslator.extensions.createRecursively
import com.miquido.stringstranslator.model.configuration.Web
import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.TranslationModel
import com.miquido.stringstranslator.parsing.spreadsheet.StringHtmlAwareEscaper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.slf4j.Logger
import java.io.File

class WebStringWriter : StringWriter, KoinComponent {

    private val logger: Logger by inject()
    private val gson: Gson by inject()
    private val htmlAwareEscaper: StringHtmlAwareEscaper by inject { parametersOf(Web.WEB_ESCAPE_SYMBOLS_MAP) }

    override fun write(
        singleStrings: Map<LanguageCode, List<TranslationModel>>?,
        pluralStrings: Map<LanguageCode, List<PluralTranslationModel>>?,
        output: String
    ) {
        writeSingleStringsDataToFile(singleStrings, output)
        writePluralStringsDataToFile(pluralStrings, output)
    }

    private fun writePluralStringsDataToFile(
        translations: Map<LanguageCode, List<PluralTranslationModel>>?,
        output: String
    ) {

        translations?.keys?.forEach { langCode ->
            val translationFile = getStringsFileForLang(
                langCode,
                output,
                Web().getPluralStringsFileName()
            )
            translationFile.printWriter().use {
                it.write(htmlAwareEscaper.escape(gson.toJson(translations[langCode])))
            }
            logger.info("[Web-plural] Wrote translations for language \"$langCode\"")
        }
    }

    private fun writeSingleStringsDataToFile(
        translations: Map<LanguageCode, List<TranslationModel>>?,
        output: String
    ) {

        translations?.keys?.forEach { langCode ->
            val translationFile = getStringsFileForLang(
                langCode,
                output,
                Web().getSingleStringsFileName()
            )
            translationFile.printWriter().use {
                it.write(htmlAwareEscaper.escape(gson.toJson(translations[langCode])))
            }
            logger.info("[Web] Wrote translations for language \"$langCode\"")
        }
    }

    private fun getStringsFileForLang(
        langCode: LanguageCode,
        output: String,
        singleStringsFileName: String
    ): File {

        return File("$output/$LANGUAGE_PREFIX$langCode/$singleStringsFileName")
            .apply {
                createRecursively()
            }
    }

    companion object {
        private const val LANGUAGE_PREFIX = "lang-"
    }
}
