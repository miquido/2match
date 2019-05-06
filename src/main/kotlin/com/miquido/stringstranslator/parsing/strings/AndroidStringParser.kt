package com.miquido.stringstranslator.parsing.strings

import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.parsing.*
import com.miquido.stringstranslator.model.parsing.strings.StringsFilePath
import com.miquido.stringstranslator.model.parsing.strings.StringsFilePathFactory
import com.miquido.stringstranslator.model.translations.AndroidTranslationModel
import com.miquido.stringstranslator.model.translations.PluralQualifier
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.parsing.xml.XmlParser
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.File

class AndroidStringParser : StringParser, KoinComponent {

    private val stringsFilePathFactory: StringsFilePathFactory by inject()
    private val logger: Logger by inject()

    override fun parseStringsFile(inputStringPath: String, baseLanguageCode: String)
            : ParsedStringTranslationModel {

        return ParsedStringTranslationModel(
                generateStringsModel(inputStringPath, baseLanguageCode).singleStringSet,
                generateStringsModel(inputStringPath, baseLanguageCode).pluralStringSet)
    }

    private fun generateStringsModel(input: String, baseLanguageCode: String)
            : ParsedStringTranslationModel {

        val data = ParsedStringTranslationModel()
        File(input).walkTopDown()
                .map { it.invariantSeparatorsPath }
                .filter { it.matches(Regex(STRING_FILE_PATTERN)) }
                .filterNotNull()
                .forEach {
                    val stringsFilePath = stringsFilePathFactory
                            .getStringsFilePath(Android(), it)
                    val languageCode = stringsFilePath.getLanguageCodeFromPath(baseLanguageCode)

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
        val pluralStringModel = HashMap<String, PluralTranslationModel>()

        parsePluralStringResources(path.value).stringsList.forEach { stringModel ->
            val pluralsQualifierMap: HashMap<PluralQualifier, String> = hashMapOf()

            stringModel.quantityList.forEach { quantityValue ->
                val pluralQualifier =
                        try {
                            PluralQualifier.valueOf(quantityValue.quantity.toUpperCase())
                        } catch (exception: IllegalArgumentException) {
                            logger.error(
                                    "Could not find quantity constant for: ${quantityValue.quantity}" +
                                            "Possible values include: " +
                                            PluralQualifier.values().joinToString(separator = ","),
                                    exception
                            )
                            null
                        }

                if (pluralQualifier != null) pluralsQualifierMap[pluralQualifier] = quantityValue.text
            }
            if (stringModel.name.isNotEmpty()) {
                pluralStringModel[stringModel.name] =
                        PluralTranslationModel(
                                stringModel.name,
                                pluralsQualifierMap
                        )
            }
        }
        return PluralStringValuesModel(pluralStringModel)
    }

    private fun takeSingleStringsFromFile(path: StringsFilePath): SingleStringValuesModel {
        val listValuesDirectories = SingleStringValuesModel()
        parseStringResources(path.value).stringsList.forEach {
            if (it.name.isNotEmpty()) {
                listValuesDirectories.singleStringValue[it.name] =
                        AndroidTranslationModel(
                                it.name,
                                it.text,
                                it.translatable,
                                it.formatted
                        )
            }
        }
        return listValuesDirectories
    }

    private fun parseStringResources(input: String): StringResources {
        return XmlParser(StringResources::class.java).parse(input)
    }

    private fun parsePluralStringResources(input: String): StringPluralsResources {
        return XmlParser(StringPluralsResources::class.java).parse(input)
    }

    private companion object {
        private const val STRING_FILE_PATTERN = ".*values.*/strings.*\\.xml"
    }
}
