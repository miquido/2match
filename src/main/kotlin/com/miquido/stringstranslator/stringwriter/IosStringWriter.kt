package com.miquido.stringstranslator.stringwriter

import com.google.gson.Gson
import com.miquido.stringstranslator.extensions.createRecursively
import com.miquido.stringstranslator.model.configuration.Ios
import com.miquido.stringstranslator.model.parsing.Localization
import com.miquido.stringstranslator.model.parsing.StringLocalization
import com.miquido.stringstranslator.model.parsing.StringUnit
import com.miquido.stringstranslator.model.parsing.Substitution
import com.miquido.stringstranslator.model.parsing.Variations
import com.miquido.stringstranslator.model.parsing.XcStrings
import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralQualifier
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.model.translations.TranslationModel
import com.miquido.stringstranslator.parsing.spreadsheet.StringHtmlAwareEscaper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.slf4j.Logger
import java.io.File

class IosStringWriter(private val baseLanguageCode: String) : StringWriter, KoinComponent {

    private val gson: Gson by inject()
    private val htmlAwareEscaper: StringHtmlAwareEscaper by inject { parametersOf(Ios.ESCAPE_SYMBOLS_MAP) }
    private val logger: Logger by inject()

    override fun write(
        singleStrings: Map<LanguageCode, List<TranslationModel>>?,
        pluralStrings: Map<LanguageCode, List<PluralTranslationModel>>?,
        output: String
    ) {
        val translations = getSingleTranslations(singleStrings) + getPluralTranslations(pluralStrings)
        val xcStringsData = XcStrings(
            sourceLanguage = baseLanguageCode,
            strings = translations.toSortedMap(),
            version = VERSION
        )

        File("$output${File.separator}${Ios().getSingleStringsFileName()}")
            .apply {
                createRecursively()
                logger.info("Created file $absolutePath")
            }
            .printWriter()
            .use { it.println(gson.toJson(xcStringsData)) }
    }

    private fun getSingleTranslations(
        singleStrings: Map<LanguageCode, List<TranslationModel>>?
    ): Map<String, StringLocalization> = singleStrings
        ?.flatMap { (languageCode, translations) -> translations.map { it.key to Pair(languageCode, it.value) } }
        ?.groupBy({ it.first }, { it.second })
        ?.entries
        ?.associate {
            it.key to StringLocalization(
                localizations = it.value.associate { (languageCode, value) ->
                    languageCode to Localization(
                        stringUnit = StringUnit(
                            state = TRANSLATED_STATE,
                            value = htmlAwareEscaper.escape(value)
                        )
                    )
                }
            )
        } ?: emptyMap()

    private fun getPluralTranslations(
        pluralStrings: Map<LanguageCode, List<PluralTranslationModel>>?
    ): Map<String, StringLocalization> = pluralStrings
        ?.flatMap { (languageCode, translations) -> translations.map { it.key to Pair(languageCode, it.pluralsMap) } }
        ?.groupBy({ it.first }, { it.second })
        ?.entries
        ?.associate {
            it.key to StringLocalization(
                localizations = it.value.associate { (languageCode, plurals) ->
                    languageCode to plurals.toPluralLocalization()
                }
            )
        } ?: emptyMap()

    private fun Map<PluralQualifier, String>.toPluralLocalization() = Localization(
        stringUnit = StringUnit(
            state = TRANSLATED_STATE,
            value = "%#@$VALUE_SUBSTITUTION@"
        ),
        /* Using substitutions allows for having plurals without parameters
        * https://forums.developer.apple.com/forums/thread/737329?answerId=764796022#764796022 */
        substitutions = mapOf(
            VALUE_SUBSTITUTION to Substitution(
                argNum = 1,
                formatSpecifier = PLURAL_FORMAT_SPECIFIER,
                variations = Variations(
                    plural = entries.associate {
                        it.key.toString() to Localization(
                            stringUnit = StringUnit(
                                state = TRANSLATED_STATE,
                                value = htmlAwareEscaper.escape(it.value)
                            )
                        )
                    }
                )
            )
        )
    )

    private companion object {
        const val TRANSLATED_STATE = "translated"
        const val VALUE_SUBSTITUTION = "value"
        const val PLURAL_FORMAT_SPECIFIER = "lld"
        const val VERSION = "1.0"
    }
}
