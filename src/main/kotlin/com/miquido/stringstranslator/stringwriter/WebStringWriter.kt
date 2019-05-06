package com.miquido.stringstranslator.stringwriter

import com.google.gson.Gson
import com.miquido.stringstranslator.extensions.createRecursively
import com.miquido.stringstranslator.extensions.escape
import com.miquido.stringstranslator.model.configuration.Web
import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.TranslationModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.File

class WebStringWriter : StringWriter, KoinComponent {

    private val logger: Logger by inject()
    private val gson: Gson by inject()

    override fun writePluralStringsDataToFile(
            translations: MutableMap<LanguageCode, MutableList<PluralTranslationModel>>?,
            output: String) {

        translations?.keys?.forEach { langCode ->
            val translationFile = getStringsFileForLang(
                    langCode,
                    output,
                    Web().getPluralStringsFileName()
            )
            translationFile.printWriter().use {
                it.write(gson.toJson(translations[langCode]).escape(Web().getEscapeMap()))
            }
            logger.info("[Web-plural] Wrote translations for language \"$langCode\"")
        }
    }

    override fun writeSingleStringsDataToFile(
            translations: MutableMap<LanguageCode, MutableList<TranslationModel>>?,
            output: String) {

        translations?.keys?.forEach { langCode ->
            val translationFile = getStringsFileForLang(
                    langCode,
                    output,
                    Web().getSingleStringsFileName()
            )
            translationFile.printWriter().use {
                it.write(gson.toJson(translations[langCode]).escape(Web().getEscapeMap()))
            }
            logger.info("[Web] Wrote translations for language \"$langCode\"")
        }
    }

    private fun getStringsFileForLang(
            langCode: LanguageCode,
            output: String,
            singleStringsFileName: String): File {

        return File("$output/$LANGUAGE_PREFIX$langCode/$singleStringsFileName")
                .apply {
                    createRecursively()
                }
    }

    companion object {
        private const val LANGUAGE_PREFIX = "lang-"
    }
}