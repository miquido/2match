package com.miquido.stringtranslator.parsing.spreadsheet

import com.miquido.stringstranslator.model.parsing.ParsedRawTranslationsModel
import com.miquido.stringstranslator.parsing.spreadsheet.SpreadsheetParser
import com.miquido.stringtranslator.di.testDiModules
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext

class SpreadsheetParserTests {
    private val spreadsheetParser = SpreadsheetParser()
    private lateinit var valuesFromParsedFile: ParsedRawTranslationsModel

    @Before
    fun setup() {
        StandAloneContext.startKoin(testDiModules)
        valuesFromParsedFile = spreadsheetParser.parseXlsFile(FILE_PATH)
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun `parse method should parse correct number of worksheet cols`() {
        assertEquals(
                "Parsed cols count differs from .xls cols count",
                EXPECTED_SINGLE_STRINGS_COLS_COUNT,
                valuesFromParsedFile.stringsSingle[0].size
        )
        assertEquals(
                "Parsed cols count differs from .xls cols count",
                EXPECTED_PLURAL_STRINGS_COLS_COUNT,
                valuesFromParsedFile.stringsPlural[0].size
        )
    }

    @Test
    fun `parse method should parse correct number of worksheet rows`() {
        assertEquals(
                "Parsed rows count differs from .xls row count",
                EXPECTED_SINGLE_STRINGS_ROWS_COUNT,
                valuesFromParsedFile.stringsSingle.size
        )
        assertEquals(
                "Parsed rows count differs from .xls row count",
                EXPECTED_PLURAL_STRINGS_ROWS_COUNT,
                valuesFromParsedFile.stringsPlural.size
        )
    }

    @Test
    fun `parse method should return correct value from xls file`() {
        val recordedValue = valuesFromParsedFile.stringsSingle[0][0]
        assertTrue(
                "Value after parsing is not equal to the value of the xls file",
                recordedValue == CORRECT_VALUE
        )
    }

    companion object {
        private const val FILE_PATH = "src/test/res/parsing/xls/test_translations.xlsx"
        private const val EXPECTED_SINGLE_STRINGS_COLS_COUNT = 7
        private const val EXPECTED_PLURAL_STRINGS_COLS_COUNT = 6
        private const val EXPECTED_SINGLE_STRINGS_ROWS_COUNT = 9
        private const val EXPECTED_PLURAL_STRINGS_ROWS_COUNT = 13
        private const val CORRECT_VALUE = "iosKey"
    }
}
