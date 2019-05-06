package com.miquido.stringtranslator.parsing.integration

import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.configuration.Ios
import com.miquido.stringstranslator.model.configuration.Web
import com.miquido.stringstranslator.parsing.spreadsheet.SpreadsheetParser
import com.miquido.stringstranslator.parsing.strings.StringParserFactory
import com.miquido.stringstranslator.task.DeleteFileTask
import com.miquido.stringstranslator.task.conversion.FromSpreadsheetConversionTask
import com.miquido.stringstranslator.task.conversion.ToSpreadsheetConversionTask
import com.miquido.stringtranslator.di.testDiModules
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import java.io.File
import kotlin.test.assertEquals

class IntegrationTests : KoinTest {

    private val spreadSheetParser: SpreadsheetParser by inject()
    private val stringParFactory: StringParserFactory by inject()

    @Before
    fun setup() {
        StandAloneContext.startKoin(testDiModules)
        File(TMP_INPUT_RECREATED_DIR).mkdirs()
    }

    @After
    fun tearDown() {
        DeleteFileTask(TMP_DIR).start()
        StandAloneContext.stopKoin()
    }

    @Test
    fun `converting (fromSpreadsheet-toSpreadsheet) should give same result for both platforms`() {
        setOf(Android(), Ios(), Web()).forEach {
            val fromSpreadsheetConversionTask =
                    FromSpreadsheetConversionTask(
                            ORIGINAL_INPUT_FILE,
                            INTEGRATION_TMP_RES_OUTPUT_DIR,
                            it,
                            BASE_LANGUAGE_CODE
                    )
            fromSpreadsheetConversionTask.start()

            val toSpreadsheetConversionTask =
                    ToSpreadsheetConversionTask(
                            INTEGRATION_TMP_RES_OUTPUT_DIR,
                            INTEGRATION_TMP_INPUT_RECREATED_FILE,
                            it,
                            BASE_LANGUAGE_CODE
                    )
            toSpreadsheetConversionTask.start()

            val originalInput =
                    spreadSheetParser.parseXlsFile(ORIGINAL_INPUT_FILE)
            val recreatedInput =
                    spreadSheetParser.parseXlsFile(INTEGRATION_TMP_INPUT_RECREATED_FILE)

            assertEquals(originalInput.stringsSingle.size, recreatedInput.stringsSingle.size,
                    "Recreated input for single strings " +
                            "has different single strings count for platform ${it.getName()}")
        }
    }

    @Test
    fun `converting (toSpreadsheet-fromSpreadsheet) should give same result for both platforms`() {
        setOf(Android(), Ios(), Web()).forEach {
            val toSpreadsheetConversionTask =
                    ToSpreadsheetConversionTask(
                            ORIGINAL_INPUT_RES_DIR,
                            INTEGRATION_TMP_XLS_OUTPUT,
                            it,
                            BASE_LANGUAGE_CODE
                    )
            toSpreadsheetConversionTask.start()

            val fromSpreadsheetConversionTask =
                    FromSpreadsheetConversionTask(
                            INTEGRATION_TMP_XLS_OUTPUT,
                            TMP_INPUT_RECREATED_DIR,
                            it,
                            BASE_LANGUAGE_CODE
                    )
            fromSpreadsheetConversionTask.start()

            val stringParser = stringParFactory.getStringParser(it)
            val originalInput =
                    stringParser.parseStringsFile(
                            ORIGINAL_INPUT_RES_DIR,
                            BASE_LANGUAGE_CODE
                    )
            val recreatedInput =
                    stringParser.parseStringsFile(
                            TMP_INPUT_RECREATED_DIR,
                            BASE_LANGUAGE_CODE
                    )

            assertEquals(originalInput.singleStringSet.singleString.keys.size,
                    recreatedInput.singleStringSet.singleString.keys.size,
                    "Recreated input for single strings " +
                            "has different single strings count for platform ${it.getName()}")
        }
    }

    companion object {
        private const val BASE_LANGUAGE_CODE = "en"
        private const val TMP_DIR = "src/test/res/integration/tmp/"
        private const val TMP_INPUT_RECREATED_DIR = "$TMP_DIR/input-recreated/"
        private const val ORIGINAL_INPUT_FILE = "src/test/res/integration/input.xlsx"
        private const val ORIGINAL_INPUT_RES_DIR = "src/test/res/integration/input-res"
        private const val INTEGRATION_TMP_RES_OUTPUT_DIR = "$TMP_DIR/res"
        private const val INTEGRATION_TMP_XLS_OUTPUT = "$TMP_DIR/output.xlsx"
        private const val INTEGRATION_TMP_INPUT_RECREATED_FILE =
                "$TMP_INPUT_RECREATED_DIR/input_recreated.xlsx"

    }
}