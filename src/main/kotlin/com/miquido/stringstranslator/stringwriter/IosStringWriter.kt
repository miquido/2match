package com.miquido.stringstranslator.stringwriter

import com.dd.plist.NSDictionary
import com.miquido.stringstranslator.parsing.spreadsheet.StringHtmlAwareEscape
import com.miquido.stringstranslator.extensions.createRecursively
import com.miquido.stringstranslator.model.configuration.Ios
import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.TranslationModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.File
import java.io.PrintWriter

class IosStringWriter(private val baseLanguageCode: String) : StringWriter, KoinComponent {

    private val logger: Logger by inject()

    override fun writePluralStringsDataToFile(
            translations: MutableMap<LanguageCode, MutableList<PluralTranslationModel>>?,
            output: String) {

        translations?.forEach {
            val root = NSDictionary()
            it.value.map { translationModel ->
                val stringDict = NSDictionary()
                stringDict.put(Ios.KEY_LOCALIZED_STRING, Ios.VALUE_LOCALIZED_STRING)
                val pluralDict = NSDictionary()
                pluralDict.put(Ios.KEY_FORMAT_SPEC, Ios.VALUE_FORMAT_SPEC)
                pluralDict.put(Ios.KEY_FORMAT_VALUE_TYPE, Ios.VALUE_FORMAT_VALUE_TYPE)
                translationModel.pluralsMap.forEach {
                    pluralDict.put(it.key.toString(),
                            StringHtmlAwareEscape(it.value, Ios.ESCAPE_SYMBOLS_MAP).value())
                }
                stringDict.put(Ios.VALUE, pluralDict)

                root.put(translationModel.key, stringDict)
            }
            getStringsFileForLangPrintWriter(
                    it.key,
                    output,
                    Ios().getPluralStringsFileName()
            ).use { out ->
                out.println(root.toXMLPropertyList())
            }
            logger.info("[iOS-plural] Wrote translations for language \"${it.key}\"")
        }
    }

    private fun getStringsFileForLangPrintWriter(
            languageCode: LanguageCode,
            baseOutputDir: String,
            stringsFileName: String): PrintWriter {

        val folderName = if (baseLanguageCode == languageCode) "Base" else languageCode
        val file = File("$baseOutputDir${File.separator}" +
                "$folderName.lproj${File.separator}" +
                stringsFileName
        )
        file.createRecursively()
        logger.info("Created file ${file.absolutePath}")
        return file.printWriter()
    }

    override fun writeSingleStringsDataToFile(
            translations: MutableMap<LanguageCode, MutableList<TranslationModel>>?,
            output: String) {

        translations?.forEach { languageTranslations ->
            getStringsFileForLangPrintWriter(
                    languageTranslations.key,
                    output,
                    Ios().getSingleStringsFileName()
            ).use { out ->
                languageTranslations.value.map { translationModel ->
                    Ios.SINGLE_STRING_FORMAT.format(
                            translationModel.key,
                            StringHtmlAwareEscape(translationModel.value, Ios.ESCAPE_SYMBOLS_MAP).value()
                    )
                }.forEach {
                    out.println(it)
                }
            }
            logger.info("[iOS] Wrote translations for language \"${languageTranslations.key}\"")
        }
    }
}