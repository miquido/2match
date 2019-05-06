package com.miquido.stringstranslator.conversion

import com.miquido.stringstranslator.extensions.createNewRowOrUseExisting
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.model.parsing.SingleStringSetModel
import com.miquido.stringstranslator.model.translations.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger

class SingleStringsToSpreadsheetConverter(platform: Platform, baseLanguageCode: String)
    : StringsToSpreadsheetConverter(platform, baseLanguageCode), KoinComponent {

    private val logger: Logger by inject()
    private val singleStringSheet = workbook.createSheet(SINGLE_STRINGS_SHEET_NAME)

    fun convertSingleStringModel(stringsModel: SingleStringSetModel): XSSFWorkbook {
        fillDataForDefaultLanguage(stringsModel)
        val baseLangValuesSize = stringsModel
                .singleString[baseLanguageCode]
                ?.singleStringValue?.size ?: 0
        val mapExceptDefaultLanguage = stringsModel
                .singleString
                .filterNot { it.key == baseLanguageCode }
        fillDataForRemainingLanguages(
                SingleStringSetModel(HashMap(mapExceptDefaultLanguage)),
                baseLangValuesSize
        )
        return workbook
    }

    private fun fillDataForDefaultLanguage(stringsModel: SingleStringSetModel) {
        val singleStringKeyValues = stringsModel
                .singleString[baseLanguageCode]
                ?.singleStringValue

        if (singleStringKeyValues?.size == 0) {
            logger.warn("No single string keys in spreadsheet for ${platform.getName()} platform")
        }

        singleStringKeyValues?.values?.forEachIndexed { index, translationModel ->
            val currentRow = VALUE_ROW + index
            val row = singleStringSheet.createNewRowOrUseExisting(currentRow)
            val rowValuesList = mutableListOf<String>()
            when (translationModel) {
                is AndroidTranslationModel -> {
                    rowValuesList.addAll(mutableListOf(
                            translationModel.key,
                            translationModel.isTranslatable.toString(),
                            translationModel.isFormatted.toString(),
                            EMPTY_VALUE,
                            translationModel.value)
                    )
                }
                is IosTranslationModel -> {
                    rowValuesList.add(translationModel.key)
                    for (i in 1 until Platform.FIRST_SINGLE_STRINGS_TRANSLATION_COLUMN_INDEX) {
                        rowValuesList.add(EMPTY_VALUE)
                    }
                    rowValuesList.add(translationModel.value)
                }
                is WebTranslationModel -> {
                    rowValuesList.addAll(mutableListOf(
                            translationModel.key,
                            translationModel.value)
                    )
                }
            }
            fillValues(ListValuesType(rowValuesList), row, platform.getSingleKeyColumnIndex())
        }
        fillHeaders(ListType(BASIC_HEADERS), singleStringSheet)
        fillHeaders(
                SingleType(baseLanguageCode, Platform.FIRST_SINGLE_STRINGS_TRANSLATION_COLUMN_INDEX),
                singleStringSheet
        )
    }

    private fun fillDataForRemainingLanguages(data: SingleStringSetModel, defaultLangValuesSize: Int) {
        for (i in 0 until defaultLangValuesSize) {
            val currentRowIndex = VALUE_ROW + i
            val row = singleStringSheet.createNewRowOrUseExisting(currentRowIndex)
            val cellKeyValue = row.getCell(platform.getSingleKeyColumnIndex())?.stringCellValue.orEmpty()

            data.singleString.keys.forEachIndexed { index, languageCode ->
                data.singleString[languageCode]
                        ?.singleStringValue
                        ?.forEach { key, translationModel ->
                            if (cellKeyValue == key) {
                                fillHeaders(
                                        SingleType(languageCode, FIRST_NOT_DEFAULT_LANGUAGE_INDEX + index),
                                        singleStringSheet
                                )
                                fillValues(
                                        SingleValueType(translationModel.value),
                                        row,
                                        FIRST_NOT_DEFAULT_LANGUAGE_INDEX + index
                                )
                            }
                        }
            }
        }
    }

    companion object {
        const val SINGLE_STRINGS_SHEET_NAME = "strings-single"
        private const val VALUE_ROW = 1
        private const val FIRST_NOT_DEFAULT_LANGUAGE_INDEX =
                Platform.FIRST_SINGLE_STRINGS_TRANSLATION_COLUMN_INDEX + 1
        private const val EMPTY_VALUE = ""
        private val BASIC_HEADERS = mutableListOf(
                "iosKey", "androidKey", "isTranslatable", "isFormatted", "webKey"
        )
    }
}


