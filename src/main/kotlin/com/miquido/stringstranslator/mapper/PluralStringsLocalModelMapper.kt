package com.miquido.stringstranslator.mapper

import com.miquido.stringstranslator.functions.mapOfNonEmptyStrings
import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.configuration.Ios
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.model.configuration.Web
import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralQualifier
import com.miquido.stringstranslator.model.translations.PluralStringsTranslations
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import org.slf4j.Logger

fun List<List<String>>.mapToPluralStringsLocalModel(platform: Platform, logger: Logger)
        : MutableMap<LanguageCode, MutableList<PluralTranslationModel>>? {

    val languageCodesRow = firstOrNull()
    val languageCodes = languageCodesRow?.slice(
            Platform.FIRST_PLURAL_STRINGS_TRANSLATION_COLUMN_INDEX..(languageCodesRow.lastIndex))
    val translations = PluralStringsTranslations(mutableMapOf())
    translations.platforms[Ios::class.java.name] = mutableMapOf()
    translations.platforms[Android::class.java.name] = mutableMapOf()
    translations.platforms[Web::class.java.name] = mutableMapOf()

    languageCodes?.forEach {
        translations.platforms[Ios::class.java.name]?.put(it, mutableListOf())
        translations.platforms[Android::class.java.name]?.put(it, mutableListOf())
        translations.platforms[Web::class.java.name]?.put(it, mutableListOf())
    }

    val iterator = listIterator()
    if (iterator.hasNext()) iterator.next() //skip headers
    while (iterator.hasNext()) {
        val rowZero = iterator.next()
        val rowOne = iterator.next()
        val rowTwo = iterator.next()
        val rowFew = iterator.next()
        val rowMany = iterator.next()
        val rowOther = iterator.next()

        languageCodes?.forEachIndexed { languageCodeIndex, value ->
            val translationPositionColumnIndex =
                    languageCodeIndex + Platform.FIRST_PLURAL_STRINGS_TRANSLATION_COLUMN_INDEX
            val translationKey = rowZero[platform.getPluralKeyColumnIndex()]

            val pluralsMap = mapOfNonEmptyStrings(
                    Pair(PluralQualifier.ZERO, rowZero[translationPositionColumnIndex]),
                    Pair(PluralQualifier.ONE, rowOne[translationPositionColumnIndex]),
                    Pair(PluralQualifier.TWO, rowTwo[translationPositionColumnIndex]),
                    Pair(PluralQualifier.FEW, rowFew[translationPositionColumnIndex]),
                    Pair(PluralQualifier.MANY, rowMany[translationPositionColumnIndex]),
                    Pair(PluralQualifier.OTHER, rowOther[translationPositionColumnIndex])
            )

            if (translationKey.isNotBlank()) {
                if (pluralsMap.isNotEmpty()) {
                    val translationModel = PluralTranslationModel(translationKey, pluralsMap)
                    translations.platforms[platform::class.java.name]
                            ?.get(value)
                            ?.add(translationModel)
                } else {
                    logger.warn("Missing translations value on row ${iterator.nextIndex() - 6} " +
                            "for language ${languageCodes[languageCodeIndex]} key $translationKey")
                }
            }
        }
    }
    val localModel = translations.platforms[platform::class.java.name]
    if (localModel.isNullOrEmpty()) {
        logger.warn("No plural string keys in local model for ${platform.getName()} platform")
    }
    return localModel
}