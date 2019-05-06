package com.miquido.stringstranslator.parsing.spreadsheet

import com.miquido.stringstranslator.extensions.isEmpty
import com.miquido.stringstranslator.model.parsing.ParsedRawTranslationsModel
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.File

//TODO consider moving model mapping extensions to mapper classes implementing common interface
class SpreadsheetParser : KoinComponent {

    private val logger: Logger by inject()

    fun parseXlsFile(filePath: String): ParsedRawTranslationsModel {
        val file = File(filePath)
        logger.info("Parsing \"${file.absolutePath}...\"")
        return WorkbookFactory.create(file).use {
            val singleStringsSheet = it.getSheetAt(SINGLE_STRINGS_SHEET_POSITION)
            val pluralStringsSheet = it.getSheetAt(PLURAL_STRINGS_SHEET_POSITION)
            ParsedRawTranslationsModel(
                    parseSheet(singleStringsSheet),
                    parseSheet(pluralStringsSheet)
            )
        }
    }

    private fun parseSheet(workSheet: Sheet?): List<List<String>> {
        val rawWorkSheet = mutableListOf<MutableList<String>>()
        val dataFormatter = DataFormatter()

        workSheet?.let { sheet ->
            val workSheetColumnsCount = sheet.getRow(0)
                    .count { it.cellType != CellType.BLANK && it.stringCellValue.isNotBlank() }
            for (row in sheet) {
                if (row.isEmpty()) break
                val dataRow = mutableListOf<String>()
                (0 until workSheetColumnsCount)
                        .map { columnIndex -> row.getCell(columnIndex) }
                        .forEach { dataRow.add(dataFormatter.formatCellValue(it)) }
                rawWorkSheet.add(dataRow)
            }
        }
        return rawWorkSheet
    }

    companion object {
        private const val SINGLE_STRINGS_SHEET_POSITION = 0
        private const val PLURAL_STRINGS_SHEET_POSITION = 1
    }
}

