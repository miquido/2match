package com.miquido.stringstranslator.task.conversion

import com.miquido.stringstranslator.conversion.PluralStringsToSpreadsheetConverter
import com.miquido.stringstranslator.conversion.SingleStringsToSpreadsheetConverter
import com.miquido.stringstranslator.extensions.copyValuesTo
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.parsing.strings.StringParserFactory
import com.miquido.stringstranslator.task.InputOutputTask
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.FileOutputStream

class ToSpreadsheetConversionTask(
        input: String,
        output: String,
        private val platform: Platform,
        private val baseLanguageCode: String
) : InputOutputTask(input, output), KoinComponent {

    private val stringParserFactory: StringParserFactory by inject()
    private val logger: Logger by inject()

    override fun start() {
        logger.info("Parsing $input to $output")
        val parser = stringParserFactory.getStringParser(platform)
        val parsedStringsFile = parser.parseStringsFile(input, baseLanguageCode)
        val singleStringsWorkbook = SingleStringsToSpreadsheetConverter(platform, baseLanguageCode)
                .convertSingleStringModel(parsedStringsFile.singleStringSet)
        val pluralStringsWorkbook = PluralStringsToSpreadsheetConverter(platform, baseLanguageCode)
                .convertPluralStringModel(parsedStringsFile.pluralStringSet)
        mergeSheetsIntoFinalWorkbook(output,
                singleStringsWorkbook.getSheet(SingleStringsToSpreadsheetConverter.SINGLE_STRINGS_SHEET_NAME),
                pluralStringsWorkbook.getSheet(PluralStringsToSpreadsheetConverter.PLURAL_STRINGS_SHEET_NAME))
        logger.info("Done!")
    }

    private fun mergeSheetsIntoFinalWorkbook(
            output: String,
            singleStringsSheetSource: XSSFSheet,
            pluralStringsSheetSource: XSSFSheet) {

        val mergedWorkBook = XSSFWorkbook()
        val newSingleStringsSheet = mergedWorkBook.createSheet(singleStringsSheetSource.sheetName)
        val newPluralStringsSheet = mergedWorkBook.createSheet(pluralStringsSheetSource.sheetName)
        singleStringsSheetSource.copyValuesTo(newSingleStringsSheet)
        pluralStringsSheetSource.copyValuesTo(newPluralStringsSheet)

        FileOutputStream(output).use { workbookFile ->
            mergedWorkBook.use { workBook ->
                workBook.write(workbookFile)
            }
        }
    }
}




