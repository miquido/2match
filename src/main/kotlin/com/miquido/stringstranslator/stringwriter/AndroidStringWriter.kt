package com.miquido.stringstranslator.stringwriter

import com.miquido.stringstranslator.extensions.createRecursively
import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.translations.AndroidTranslationModel
import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.TranslationModel
import com.miquido.stringstranslator.parsing.spreadsheet.StringHtmlAwareEscaper
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.File
import java.io.PrintWriter

class AndroidStringWriter(private val baseLanguageCode: String) : StringWriter, KoinComponent {

    private val logger: Logger by inject()
    private val htmlAwareEscaper: StringHtmlAwareEscaper by inject { parametersOf(Android.ANDROID_ESCAPE_SYMBOLS_MAP) }

    override fun writePluralStringsDataToFile(
            translations: MutableMap<LanguageCode, MutableList<PluralTranslationModel>>?,
            output: String) {

        translations?.keys?.forEach { langCode ->
            getStringsFileForLangPrintWriter(
                    langCode,
                    output,
                    Android().getPluralStringsFileName()
            ).use { out ->
                out.println(Android.RESOURCES_OPEN_TAG)
                translations[langCode]?.forEach {
                    out.println(Android.STRING_PLURAL_PARENT_TAG_START_FORMAT.format(it.key))
                    it.pluralsMap.forEach { pluralEntry ->
                        out.println(Android.STRING_PLURAL_ITEM_FORMAT
                                .format(
                                        pluralEntry.key.toString(),
                                        htmlAwareEscaper.escape(pluralEntry.value)
                                )
                        )
                    }
                    out.println(Android.STRING_PLURAL_PARENT_TAG_END)
                }
                out.println(Android.RESOURCES_CLOSE_TAG)
            }
            logger.info("[Android-plural] Wrote translations for language \"$langCode\"")
        }
    }

    private fun getStringsFileForLangPrintWriter(
            langCode: LanguageCode,
            baseOutputDir: String,
            stringsFileName: String): PrintWriter {

        val valuesDirName = if (langCode == baseLanguageCode) {
            BASE_VALUES_DIR_NAME
        } else {
            SPEC_VALUES_DIR_NAME_FORMAT.format(langCode)
        }
        val langFileOutput =
                File("$baseOutputDir${File.separator}" +
                        "$valuesDirName${File.separator}" +
                        stringsFileName)
        langFileOutput.createRecursively()
        logger.info("Created file ${langFileOutput.absolutePath}")
        return langFileOutput.printWriter()
    }

    override fun writeSingleStringsDataToFile(
            translations: MutableMap<LanguageCode, MutableList<TranslationModel>>?,
            output: String) {

        translations?.keys?.forEach { langCode ->
            getStringsFileForLangPrintWriter(
                    langCode,
                    output,
                    Android().getSingleStringsFileName()
            ).use { out ->
                out.println(Android.RESOURCES_OPEN_TAG)
                translations[langCode]?.map { it as AndroidTranslationModel }?.map {
                    Android.SINGLE_STRING_FORMAT.format(it.key,
                            it.isFormatted,
                            it.isTranslatable,
                            htmlAwareEscaper.escape(it.value)
                    )
                }?.forEach { out.println(it) }
                out.println(Android.RESOURCES_CLOSE_TAG)
            }
            logger.info("[Android] Wrote translations for language \"$langCode\"")
        }
    }

    companion object {
        private const val BASE_VALUES_DIR_NAME = "values"
        private const val SPEC_VALUES_DIR_NAME_FORMAT = "values-%s"
    }
}