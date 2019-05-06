package com.miquido.stringtranslator.parsing.strings.ios

import com.miquido.stringstranslator.parsing.strings.IosStringParser
import com.miquido.stringtranslator.di.testDiModules
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.startKoin
import kotlin.test.assertEquals

class IosSingleStringParserTest {

    private lateinit var stringParser: IosStringParser

    @Before
    fun setup() {
        startKoin(testDiModules)
        stringParser = IosStringParser()
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun `string files model should have correct languages count`() {
        val parsedStringsModel =
                stringParser.parseStringsFile(RES_IOS_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        assertEquals(
                EXPECTED_LANGUAGES_COUNT,
                parsedStringsModel.singleStringSet.singleString.keys.size,
                "Languages count is not equal to expected"
        )
    }

    @Test
    fun `string files model should have correct keys count`() {
        val parsedStringsModel =
                stringParser.parseStringsFile(RES_IOS_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        parsedStringsModel.singleStringSet.singleString.values.forEach {
            assertEquals(
                    EXPECTED_ALL_KEYS_COUNT,
                    it.singleStringValue.size,
                    "Missing a translation key for ${it.singleStringValue}"
            )
        }
    }

    @Test
    fun `string files model should values even with missing keys`() {
        val parsedStringsModel =
                stringParser.parseStringsFile(RES_IOS_MISSING_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        val baseLangSingleStrings =
                parsedStringsModel
                        .singleStringSet
                        .singleString[BASE_LANGUAGE_ALL_KEYS]
                        ?.singleStringValue
        val missingKeysLangSingleStrings =
                parsedStringsModel
                        .singleStringSet
                        .singleString[LANGUAGE_MISSING_KEYS]
                        ?.singleStringValue
        assertEquals(
                EXPECTED_ALL_KEYS_COUNT,
                baseLangSingleStrings?.keys?.size,
                "Base language does not have all required keys"
        )
        assertEquals(
                EXPECTED_MISSING_KEYS_COUNT,
                missingKeysLangSingleStrings?.keys?.size,
                "Language with missing keys has incorrect parsed keys count"
        )
    }

    @Test
    fun `languages with missing keys should have integrity with base language`() {
        val parsedStringsModel =
                stringParser.parseStringsFile(RES_IOS_MISSING_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        val baseLangSingleStrings =
                parsedStringsModel
                        .singleStringSet
                        .singleString[BASE_LANGUAGE_ALL_KEYS]
                        ?.singleStringValue
        val missingKeysLangSingleStrings =
                parsedStringsModel
                        .singleStringSet
                        .singleString[LANGUAGE_MISSING_KEYS]
                        ?.singleStringValue

        val commonSubsetSize =
                baseLangSingleStrings
                        ?.filterKeys {
                            missingKeysLangSingleStrings
                                ?.keys
                                ?.contains(it) == true }
                        ?.size
        assertEquals(
                EXPECTED_MISSING_KEYS_COUNT,
                commonSubsetSize,
                "Not all missing keys language keys are present in base language with all keys"
        )
    }


    companion object {
        private const val RES_IOS_BASE_DIR_PATH = "src/test/res/parsing/strings/res-ios"
        private const val RES_IOS_MISSING_BASE_DIR_PATH = "src/test/res/parsing/strings/res-ios-missing"
        private const val EXPECTED_LANGUAGES_COUNT = 2
        private const val EXPECTED_ALL_KEYS_COUNT = 8
        private const val EXPECTED_MISSING_KEYS_COUNT = 4
        private const val BASE_LANGUAGE_ALL_KEYS = "en"
        private const val LANGUAGE_MISSING_KEYS = "pl"
    }
}