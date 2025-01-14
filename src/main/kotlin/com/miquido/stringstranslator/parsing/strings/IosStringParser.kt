package com.miquido.stringstranslator.parsing.strings

import com.google.gson.Gson
import com.miquido.stringstranslator.model.parsing.Localization
import com.miquido.stringstranslator.model.parsing.ParsedStringTranslationModel
import com.miquido.stringstranslator.model.parsing.PluralStringSetModel
import com.miquido.stringstranslator.model.parsing.PluralStringValuesModel
import com.miquido.stringstranslator.model.parsing.SingleStringSetModel
import com.miquido.stringstranslator.model.parsing.SingleStringValuesModel
import com.miquido.stringstranslator.model.parsing.XcStrings
import com.miquido.stringstranslator.model.translations.IosTranslationModel
import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralQualifier
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class IosStringParser : StringParser, KoinComponent {

    private val gson: Gson by inject()

    override fun parseStringsFile(inputStringPath: String, baseLanguageCode: String): ParsedStringTranslationModel {
        val singleStringsMap = LinkedHashMap<LanguageCode, SingleStringValuesModel>()
        val pluralStringsMap = LinkedHashMap<LanguageCode, PluralStringValuesModel>()

        File(inputStringPath).walkTopDown()
            .map { it.invariantSeparatorsPath }
            .filter { it.matches(Regex(XC_STRINGS_FILE_PATTERN)) }
            .forEach { filePath ->
                val jsonFile = File(filePath)
                val xcStrings: XcStrings = gson.fromJson(jsonFile.readText(), XcStrings::class.java)

                xcStrings.strings.entries.forEach { (key, value) ->
                    value.localizations.entries.forEach { (languageCode, localization) ->
                        val pluralTranslation = localization.getPlurals()
                        val singleTranslation = localization.stringUnit?.value
                        if (pluralTranslation != null) {
                            pluralStringsMap.computeIfAbsent(languageCode) { PluralStringValuesModel() }
                            pluralStringsMap[languageCode]
                                ?.pluralStringValue
                                ?.put(key, PluralTranslationModel(key, pluralTranslation))
                        } else if (singleTranslation != null) {
                            singleStringsMap.computeIfAbsent(languageCode) { SingleStringValuesModel() }
                            singleStringsMap[languageCode]
                                ?.singleStringValue
                                ?.put(key, IosTranslationModel(key, singleTranslation))
                        }
                    }
                }
            }

        return ParsedStringTranslationModel(
            singleStringSet = SingleStringSetModel(singleStringsMap),
            pluralStringSet = PluralStringSetModel(pluralStringsMap)
        )
    }

    private fun Localization.getPlurals(): Map<PluralQualifier, String>? =
        (variations?.plural ?: substitutions?.values?.firstOrNull()?.variations?.plural)
            ?.entries
            ?.associate { (key, value) -> PluralQualifier.valueOf(key.uppercase()) to value.stringUnit?.value.orEmpty() }
            ?.toMutableMap()

    companion object {
        private const val XC_STRINGS_FILE_PATTERN = ".*\\.xcstrings"
    }
}

