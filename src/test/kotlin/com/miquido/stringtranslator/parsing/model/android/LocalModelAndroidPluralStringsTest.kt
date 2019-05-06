package com.miquido.stringtranslator.parsing.model.android

import com.miquido.stringstranslator.mapper.mapToPluralStringsLocalModel
import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.model.parsing.ParsedRawTranslationsModel
import com.miquido.stringstranslator.model.translations.LanguageCode
import com.miquido.stringstranslator.model.translations.PluralTranslationModel
import com.miquido.stringstranslator.parsing.spreadsheet.SpreadsheetParser
import com.miquido.stringtranslator.di.testDiModules
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext
import org.slf4j.helpers.NOPLogger

class LocalModelAndroidPluralStringsTest {

    private val spreadsheetParser = SpreadsheetParser()
    private lateinit var valuesFromParsedFile: ParsedRawTranslationsModel
    private var localModel: MutableMap<LanguageCode, MutableList<PluralTranslationModel>>? = null

    @Before
    fun setup() {
        StandAloneContext.startKoin(testDiModules)
        valuesFromParsedFile = spreadsheetParser.parseXlsFile(FILE_PATH)
        localModel = valuesFromParsedFile
                .stringsPlural
                .mapToPluralStringsLocalModel(Android(), NOPLogger.NOP_LOGGER)
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun `local model should contain correct languages count`() {
        assertEquals(
                "Languages count differs from .xls count",
                EXPECTED_LANGUAGES_COUNT,
                localModel?.keys?.size
        )
    }

    @Test
    fun `local model should contain correct translations count`() {
        assertEquals(
                "Translations count differs from .xls ones",
                EXPECTED_TRANSLATIONS_COUNT,
                localModel?.values?.size
        )
    }

    @Test
    fun `correct translations count should be generated`() {
        assertEquals(
                "Plurals with no keys are present in generated file",
                EXPECTED_PLURALS_WITH_NO_KEYS_COUNT,
                localModel?.get("pl")?.firstOrNull()?.pluralsMap?.size
        )
        assertEquals(
                "Translations count differs from .xls ones",
                EXPECTED_PLURALS_WITH_ALL_KEYS_COUNT,
                localModel?.get("pl")?.get(1)?.pluralsMap?.size
        )
    }

    companion object {
        private const val FILE_PATH = "src/test/res/parsing/xls/test_translations.xlsx"
        private const val EXPECTED_TRANSLATIONS_COUNT = 2
        private const val EXPECTED_PLURALS_WITH_NO_KEYS_COUNT = 2
        private const val EXPECTED_PLURALS_WITH_ALL_KEYS_COUNT = 6
        private const val EXPECTED_LANGUAGES_COUNT = 2
    }
}