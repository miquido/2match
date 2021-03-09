package com.miquido.stringstranslator.parsing.strings

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.miquido.stringstranslator.model.configuration.Web
import com.miquido.stringstranslator.model.parsing.*
import com.miquido.stringstranslator.model.parsing.strings.StringsFilePath
import com.miquido.stringstranslator.model.parsing.strings.StringsFilePathFactory
import com.miquido.stringstranslator.model.translations.PluralQualifier
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.WebTranslationModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.File
import java.nio.charset.Charset

class WebStringParser : StringParser, KoinComponent {

    private val gson: Gson by inject()
    private val stringsFilePathFactory: StringsFilePathFactory by inject()
    private val logger: Logger by inject()

    override fun parseStringsFile(inputStringPath: String, baseLanguageCode: String)
            : ParsedStringTranslationModel {

        return ParsedStringTranslationModel(
                generateStringModel(inputStringPath, baseLanguageCode).singleStringSet,
                generateStringModel(inputStringPath, baseLanguageCode).pluralStringSet)
    }

    private fun generateStringModel(input: String, baseLangCode: String)
            : ParsedStringTranslationModel {

        val data = ParsedStringTranslationModel()
        File(input).walkTopDown()
                .map { it.invariantSeparatorsPath }
                .filter { it.matches(Regex(STRING_FILE_PATTERN)) }
                .filterNotNull()
                .forEach {
                    val stringsFilePath = stringsFilePathFactory.getStringsFilePath(Web(), it)
                    val languageCode = stringsFilePath.getLanguageCodeFromPath(baseLangCode)

                    if (stringsFilePath.isPluralStringsFilePath()) {
                        data.pluralStringSet.pluralString[languageCode] =
                                takePluralStringsFromFile(stringsFilePath)
                    } else {
                        data.singleStringSet.singleString[languageCode] =
                                takeSingleStringsFromFile(stringsFilePath)
                    }
                }
        return data
    }

    private fun takePluralStringsFromFile(path: StringsFilePath): PluralStringValuesModel {
        val pluralStringModel = LinkedHashMap<String, PluralTranslationModel>()

        parsePluralStringResources(path.value).forEach { stringWebModel ->
            val pluralsQualifierMap: HashMap<PluralQualifier, String> = hashMapOf()

            stringWebModel.pluralsMap.keys.forEach { key ->
                val pluralQualifier =
                        try {
                            PluralQualifier.valueOf(key.toUpperCase())
                        } catch (exception: IllegalArgumentException) {
                            logger.error(
                                    "Could not find quantity constant for: $key" +
                                            "Possible values include: " +
                                            PluralQualifier.values().joinToString(separator = ","),
                                    exception
                            )
                            null
                        }
                if (pluralQualifier != null) {
                    pluralsQualifierMap[pluralQualifier] = stringWebModel
                            .pluralsMap
                            .getValue(pluralQualifier.name.toLowerCase())
                }
            }

            if (stringWebModel.key.isNotEmpty()) {
                pluralStringModel[stringWebModel.key] =
                        PluralTranslationModel(stringWebModel.key, pluralsQualifierMap)
            }
        }
        return PluralStringValuesModel(pluralStringModel)
    }

    private fun takeSingleStringsFromFile(path: StringsFilePath): SingleStringValuesModel {
        val stringModel = SingleStringValuesModel()
        parseSingleStringResources(path.value).forEach {
            if (it.key.isNotEmpty()) {
                stringModel.singleStringValue[it.key] = WebTranslationModel(it.key, it.value)
            }
        }
        return stringModel
    }

    private fun parsePluralStringResources(input: String): List<PluralStringJsonModel> {
        val jsonReader = createJsonReader(input)
        return jsonReader
                .use {
                    gson.fromJson<Array<PluralStringJsonModel>>(
                            it, Array<PluralStringJsonModel>::class.java
                    )
                            .toList()
                }
    }

    private fun parseSingleStringResources(input: String): List<SingleStringJsonModel> {
        val jsonReader = createJsonReader(input)
        return jsonReader
                .use {
                    gson.fromJson<Array<SingleStringJsonModel>>(
                            it, Array<SingleStringJsonModel>::class.java
                    )
                            .toList()
                }
    }

    private fun createJsonReader(path: String) =
            JsonReader(File(path).reader(Charset.forName(CHARSET_NAME)))

    private companion object {
        private const val STRING_FILE_PATTERN = ".*/lang-.*/strings.*\\.json"
        private const val CHARSET_NAME = "UTF-8"
    }
}