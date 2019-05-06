package com.miquido.stringtranslator.parsing.model.android

import com.miquido.stringstranslator.mapper.mapToSingleStringsLocalModel
import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.parsing.ParsedRawTranslationsModel
import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.TranslationModel
import com.miquido.stringstranslator.parsing.spreadsheet.SpreadsheetParser
import com.miquido.stringtranslator.di.testDiModules
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext
import org.slf4j.helpers.NOPLogger

class LocalModelAndroidSingleStringsTest {

    private val spreadsheetParser = SpreadsheetParser()
    private lateinit var valuesFromParsedFile: ParsedRawTranslationsModel
    private var localModel: MutableMap<LanguageCode, MutableList<TranslationModel>>? = null

    @Before
    fun setup() {
        StandAloneContext.startKoin(testDiModules)
        valuesFromParsedFile = spreadsheetParser.parseXlsFile(FILE_PATH)
        localModel = valuesFromParsedFile
                .stringsSingle
                .mapToSingleStringsLocalModel(Android(), NOPLogger.NOP_LOGGER)
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun `local model list on Android platform should have only elements with keys`() {
        var allElementsHaveKey = true
        localModel?.forEach {
            it.value.forEach { t ->
                if (t.key.isBlank() || t.key.isEmpty()) allElementsHaveKey = false
            }
        }
        assertTrue(
                "In the local model there are elements without keys",
                allElementsHaveKey
        )
    }

    @Test
    fun `local model should contain correct translations count`() {
        assertEquals(
                "Translations count differs from .xls ones",
                EXPECTED_TRANSLATIONS_COUNT,
                localModel?.get("pl")?.size
        )
    }

    @Test
    fun `local model should contain correct languages count`() {
        assertEquals(
                "Languages count differs from .xls count",
                EXPECTED_LANGUAGES_COUNT,
                localModel?.keys?.size
        )
    }

    companion object {
        private const val FILE_PATH = "src/test/res/parsing/xls/test_translations.xlsx"
        private const val EXPECTED_TRANSLATIONS_COUNT = 8
        private const val EXPECTED_LANGUAGES_COUNT = 2
    }
}