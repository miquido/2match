package com.miquido.stringstranslator.conversion

import com.miquido.stringstranslator.extensions.createNewCellOrUseExisting
import com.miquido.stringstranslator.extensions.createNewRowOrUseExisting
import com.miquido.stringstranslator.extensions.escape
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.model.translations.*
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

abstract class StringsToSpreadsheetConverter(
        protected val platform: Platform,
        protected val baseLanguageCode: String) {

    protected val workbook = XSSFWorkbook()

    protected fun fillValues(collection: ValuesType, row: Row, cellIndex: Int) {
        when (collection) {
            is ListValuesType -> {
                collection.list.forEachIndexed { index, value ->
                    row.createNewCellOrUseExisting(cellIndex + index).apply {
                        setCellValue(value.escape(platform.getEscapeMap().inverse))
                    }
                }
            }

            is MapValuesType -> {
                val stringCellValue = row.getCell(platform.getPluralQualifierColumnIndex())?.stringCellValue
                collection.map.keys.forEach {
                    if (it.name == stringCellValue) {
                        row.createNewCellOrUseExisting(cellIndex).apply {
                            setCellValue(collection.map.getValue(it).escape(platform.getEscapeMap().inverse))
                        }
                    }
                }
            }

            is SingleValueType -> {
                row.createNewCellOrUseExisting(cellIndex).apply {
                    setCellValue(collection.item.escape(platform.getEscapeMap().inverse))
                }
            }
        }
    }

    protected fun fillHeaders(headerType: HeaderType, sheet: Sheet) {
        val labelRow = sheet.createNewRowOrUseExisting(LABELS_ROW)
        when (headerType) {
            is SingleType -> {
                labelRow.createCell(headerType.cellIndex).apply {
                    setCellValue(headerType.language)
                }
            }
            is ListType -> {
                headerType.list.forEachIndexed { headerIndex, value ->
                    labelRow.createCell(headerIndex).apply {
                        setCellValue(value)
                    }
                }
            }
        }
    }

    companion object {
        const val LABELS_ROW = 0
    }
}