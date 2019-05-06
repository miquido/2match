package com.miquido.stringstranslator.parsing.strings

import com.dd.plist.NSDictionary
import com.dd.plist.PropertyListParser
import com.miquido.stringstranslator.model.configuration.Ios
import com.miquido.stringstranslator.model.parsing.*
import com.miquido.stringstranslator.model.parsing.strings.StringsFilePath
import com.miquido.stringstranslator.model.parsing.strings.StringsFilePathFactory
import com.miquido.stringstranslator.model.translations.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.io.File

class IosStringParser : StringParser, KoinComponent {

    private val stringsFilePathFactory: StringsFilePathFactory by inject()

    override fun parseStringsFile(inputStringPath: String, baseLanguageCode: String)
            : ParsedStringTranslationModel {

        val singleStringsMap = HashMap<LanguageCode, SingleStringValuesModel>()
        val pluralStringsMap = HashMap<LanguageCode, PluralStringValuesModel>()
        File(inputStringPath).walkTopDown()
                .map { it.invariantSeparatorsPath }
                .filter { it.matches(Regex(STRING_FILE_PATTERN)) }
                .filterNotNull()
                .forEach {
                    val stringsFilePath = stringsFilePathFactory
                            .getStringsFilePath(Ios(), it)
                    val languageCode = stringsFilePath.getLanguageCodeFromPath(baseLanguageCode)

                    if (stringsFilePath.isPluralStringsFilePath()) {
                        pluralStringsMap[languageCode] =
                                parseValuesForPluralsFile(stringsFilePath)
                    } else {
                        singleStringsMap[languageCode] =
                                parseValuesForSingleStringsFile(stringsFilePath)
                    }
                }
        return ParsedStringTranslationModel(
                SingleStringSetModel(singleStringsMap),
                PluralStringSetModel(pluralStringsMap)
        )
    }

    private fun parseValuesForPluralsFile(filePath: StringsFilePath): PluralStringValuesModel {
        val pluralStringsValuesModel = HashMap<String, PluralTranslationModel>()
        (PropertyListParser.parse(filePath.value) as NSDictionary).forEach {
            val pluralsMap = hashMapOf<PluralQualifier, String>()
            val translationModel = PluralTranslationModel(it.key, pluralsMap)
            val localizedStringsWithFormatKey =
                    (it.value as NSDictionary)[Ios.KEY_LOCALIZED_STRING].toString()
            val localizedStringsKeyNoFormat =
                    Regex(LETTERS_ONLY_PATTERN)
                            .find(localizedStringsWithFormatKey)
                            ?.groupValues
                            ?.firstOrNull()
                            ?: throw Exception(
                                    "Cannot find value from NSStringLocalizedFormatKey " +
                                            "which is used as key for plurals map"
                            )
            ((it.value as NSDictionary).objectForKey(localizedStringsKeyNoFormat) as NSDictionary)
                    .let { pluralsDict ->
                        pluralsMap.addIfStringValuePresent(PluralQualifier.ZERO, pluralsDict)
                        pluralsMap.addIfStringValuePresent(PluralQualifier.ONE, pluralsDict)
                        pluralsMap.addIfStringValuePresent(PluralQualifier.TWO, pluralsDict)
                        pluralsMap.addIfStringValuePresent(PluralQualifier.FEW, pluralsDict)
                        pluralsMap.addIfStringValuePresent(PluralQualifier.MANY, pluralsDict)
                        pluralsMap.addIfStringValuePresent(PluralQualifier.OTHER, pluralsDict)
                    }
            if (it.key.isNotEmpty()) pluralStringsValuesModel[it.key] = translationModel
        }
        return PluralStringValuesModel(pluralStringsValuesModel)
    }

    private fun HashMap<PluralQualifier, String>.addIfStringValuePresent(
            qualifier: PluralQualifier,
            dict: NSDictionary) {

        val dictKey = qualifier.toString()
        if (dict.containsKey(dictKey)) {
            put(qualifier, dict.objectForKey(dictKey).toString())
        }
    }

    private fun parseValuesForSingleStringsFile(filePath: StringsFilePath)
            : SingleStringValuesModel {

        val iosStringsModel = IosSingleStringsModel()
        iosStringsModel.load(File(filePath.value))
        val singleStringsValuesModel = HashMap<String, TranslationModel>()
        iosStringsModel
                .map {
                    Pair(it.key, IosTranslationModel(it.key, it.value))
                }
                .forEach { if (it.first.isNotEmpty()) singleStringsValuesModel[it.first] = it.second }
        return SingleStringValuesModel(singleStringsValuesModel)
    }

    companion object {
        private const val LETTERS_ONLY_PATTERN = "[a-zA-Z]+"
        private const val STRING_FILE_PATTERN = ".*\\.lproj/.*strings.*"
    }
}
