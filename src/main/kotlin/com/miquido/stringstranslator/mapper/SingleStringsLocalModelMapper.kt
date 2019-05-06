package com.miquido.stringstranslator.mapper

import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.configuration.Ios
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.model.configuration.Web
import com.miquido.stringstranslator.model.translations.*
import org.slf4j.Logger

private const val FIRST_NON_HEADER_ROW_INDEX = 1

fun List<List<String>>.mapToSingleStringsLocalModel(platform: Platform, logger: Logger)
        : MutableMap<LanguageCode, MutableList<TranslationModel>>? {

    val languageCodesRow = firstOrNull()
    val languageCodes = languageCodesRow?.slice(
            Platform.FIRST_SINGLE_STRINGS_TRANSLATION_COLUMN_INDEX..(languageCodesRow.lastIndex)
    )
    val translations = SingleStringsTranslations(mutableMapOf())
    translations.platforms[Ios::class.java.name] = mutableMapOf()
    translations.platforms[Android::class.java.name] = mutableMapOf()
    translations.platforms[Web::class.java.name] = mutableMapOf()

    languageCodes?.forEach {
        translations.platforms[Ios::class.java.name]?.put(it, mutableListOf())
        translations.platforms[Android::class.java.name]?.put(it, mutableListOf())
        translations.platforms[Web::class.java.name]?.put(it, mutableListOf())
    }

    for (rowIndex in FIRST_NON_HEADER_ROW_INDEX until size) {
        val translationRow = get(rowIndex)
        languageCodes?.forEachIndexed { languageCodeIndex, value ->
            val translationPositionColumnIndex =
                    languageCodeIndex + Platform.FIRST_SINGLE_STRINGS_TRANSLATION_COLUMN_INDEX
            val translationKey = translationRow[platform.getSingleKeyColumnIndex()]
            val translationValue = translationRow[translationPositionColumnIndex]
            if (translationKey.isNotBlank()) {
                if (translationValue.isNotBlank()) {
                    val translationModel = when (platform) {
                        is Ios -> IosTranslationModel(translationKey, translationValue)
                        is Android -> AndroidTranslationModel(translationKey, translationValue,
                                translationRow[Android.TRANSLATABLE_COLUMN_INDEX].toBoolean(),
                                translationRow[Android.FORMATTED_COLUMN_INDEX].toBoolean())
                        is Web -> WebTranslationModel(translationKey, translationValue)
                    }
                    translations.platforms[platform::class.java.name]
                            ?.get(value)
                            ?.add(translationModel)
                } else {
                    logger.warn("Missing translation value on row ${rowIndex + 1} " +
                            "for language ${languageCodes[languageCodeIndex]} " +
                            "key $translationKey")
                }
            }

        }
    }

    val localModel = translations.platforms[platform::class.java.name]
    if (localModel.isNullOrEmpty()) {
        logger.warn("No single string keys in local model for ${platform.getName()} platform")
    }
    return localModel
}