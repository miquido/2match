package com.miquido.stringstranslator.conversion

import com.miquido.stringstranslator.extensions.createNewRowOrUseExisting
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.model.parsing.PluralStringSetModel
import com.miquido.stringstranslator.model.translations.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger

class PluralStringsToSpreadsheetConverter(platform: Platform, baseLanguageCode: String)
    : StringsToSpreadsheetConverter(platform, baseLanguageCode), KoinComponent {

    private val logger: Logger by inject()
    private val pluralStringSheet = workbook.createSheet(PLURAL_STRINGS_SHEET_NAME)

    fun convertPluralStringModel(stringsModel: PluralStringSetModel): XSSFWorkbook {
        fillDataForDefaultLanguage(stringsModel)
        val mapExceptDefaultLanguage = stringsModel
                .pluralString
                .filterNot { it.key == baseLanguageCode }
        val baseLangValuesSize = stringsModel
                .pluralString[baseLanguageCode]
                ?.pluralStringValue?.size ?: 0
        fillDataForRemainingLanguages(PluralStringSetModel(HashMap(mapExceptDefaultLanguage)), baseLangValuesSize)
        return workbook
    }

    private fun fillDataForDefaultLanguage(stringsModel: PluralStringSetModel) {
        val pluralStringValue = stringsModel
                .pluralString[baseLanguageCode]
                ?.pluralStringValue

        if (pluralStringValue?.size == 0) {
            logger.warn("No plural string keys in spreadsheet for ${platform.getName()} platform")
        }

        pluralStringValue?.values?.forEachIndexed { index, model ->
            fillDefaultValues(model, index)
        }

        fillHeaders(ListType(BASIC_HEADERS), pluralStringSheet)
        fillHeaders(
                SingleType(baseLanguageCode, Platform.FIRST_PLURAL_STRINGS_TRANSLATION_COLUMN_INDEX),
                pluralStringSheet
        )
        fillPluralQualifierCells(pluralStringValue?.keys?.size ?: 0)
    }

    private fun fillDataForRemainingLanguages(stringsModel: PluralStringSetModel, defaultLangValuesSize: Int) {
        for (i in 0 until defaultLangValuesSize) {
            val keyRowIndex = i * PLURALS_TYPE_NUMBER
            val currentRowIndex = VALUE_ROW + keyRowIndex

            val keyRow = pluralStringSheet.getRow(currentRowIndex)
            val cellKeyValue = if (keyRow != null) {
                keyRow.getCell(platform.getPluralKeyColumnIndex())?.stringCellValue.orEmpty()
            } else {
                ""
            }

            stringsModel.pluralString.keys.forEachIndexed { languageIndex, languageCode ->
                stringsModel.pluralString[languageCode]?.pluralStringValue
                        ?.forEach { pluralKey, pluralTranslationModel ->

                            fillHeaders(
                                    SingleType(languageCode, FIRST_NOT_DEFAULT_LANGUAGE_INDEX + languageIndex),
                                    pluralStringSheet
                            )

                            if (pluralKey.toLowerCase() == cellKeyValue.toLowerCase()) {
                                for (index in (0 until PLURALS_TYPE_NUMBER)) {
                                    val row = pluralStringSheet
                                            .createNewRowOrUseExisting(currentRowIndex + index)
                                    val pluralKeyValue = row
                                            .getCell(platform.getPluralQualifierColumnIndex()).stringCellValue

                                    pluralTranslationModel.pluralsMap
                                            .filter { it.key.name.toLowerCase() == pluralKeyValue.toLowerCase() }
                                            .map { it.key }
                                            .firstOrNull()
                                            .takeIf { it != null && pluralTranslationModel.pluralsMap.containsKey(it) }
                                            ?.let {
                                                fillValues(
                                                        SingleValueType(pluralTranslationModel.pluralsMap.getValue(it)),
                                                        row,
                                                        FIRST_NOT_DEFAULT_LANGUAGE_INDEX + languageIndex
                                                )
                                            }
                                }
                            }
                        }
            }
        }
    }

    private fun fillDefaultValues(pluralTranslationModel: PluralTranslationModel, indexModel: Int) {
        val pluralsSize = pluralTranslationModel.pluralsMap.size

        pluralTranslationModel.pluralsMap.keys.forEachIndexed { index, pluralQualifier ->
            val currentRowIndex = VALUE_ROW + (indexModel * pluralsSize) + index
            val row = pluralStringSheet.createNewRowOrUseExisting(currentRowIndex)

            if (index == 0) {
                fillValues(
                        SingleValueType(pluralTranslationModel.key),
                        row,
                        platform.getPluralKeyColumnIndex()
                )
            }
            fillValues(
                    SingleValueType(pluralQualifier.name),
                    row,
                    platform.getPluralQualifierColumnIndex()
            )
            fillValues(
                    MapValuesType(pluralTranslationModel.pluralsMap),
                    row,
                    Platform.FIRST_PLURAL_STRINGS_TRANSLATION_COLUMN_INDEX
            )
        }
    }

    private fun fillPluralQualifierCells(numberOfPluralKeys: Int) {
        for (i in 0 until DEFAULT_QUALIFIER_COLLECTIONS_COUNT) {
            val qualifierStartRowIndex = PLURALS_TYPE_NUMBER * i + VALUE_ROW
            PluralQualifier.values().forEachIndexed { qualifierIndex, pluralQualifier ->
                val currentRowIndex =
                        (numberOfPluralKeys * PLURALS_TYPE_NUMBER) + qualifierIndex + qualifierStartRowIndex
                val row = pluralStringSheet.createNewRowOrUseExisting(currentRowIndex)
                fillValues(
                        SingleValueType(pluralQualifier.name),
                        row,
                        platform.getPluralQualifierColumnIndex()
                )
            }
        }
    }

    companion object {
        const val PLURAL_STRINGS_SHEET_NAME = "strings-plural"
        private const val VALUE_ROW = 1
        private const val FIRST_NOT_DEFAULT_LANGUAGE_INDEX = Platform.FIRST_PLURAL_STRINGS_TRANSLATION_COLUMN_INDEX + 1
        private const val PLURALS_TYPE_NUMBER = 6
        private const val DEFAULT_QUALIFIER_COLLECTIONS_COUNT = 1000
        private val BASIC_HEADERS = mutableListOf("iosKey", "androidKey", "webKey", "plural")
    }
}

