package com.miquido.stringtranslator.parsing.strings.ios

import com.miquido.stringstranslator.parsing.strings.IosStringParser
import com.miquido.stringtranslator.di.testDiModules
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.startKoin
import kotlin.test.assertEquals

class IosPluralStringParserTest {

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
        val parsedStringModel =
                stringParser.parseStringsFile(RES_EASY_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        assertEquals(
                EXPECTED_LANGUAGES_COUNT,
                parsedStringModel.pluralStringSet.pluralString.size,
                "Languages count is not equal to expected"
        )
    }

    @Test
    fun `strings file model should have correct count of plurals collection`() {
        val parsedStringModel =
                stringParser.parseStringsFile(RES_EASY_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        assertEquals(
                EXPECTED_PLURAL_COLLECTION_COUNT,
                parsedStringModel
                        .pluralStringSet
                        .pluralString[BASE_LANGUAGE_ALL_KEYS]
                        ?.pluralStringValue
                        ?.keys
                        ?.size,
                "Plurals collection count is not equal to expected"
        )
    }

    @Test
    fun `string files model should have correct count of items in each plural collection`() {
        val parsedStringModel =
                stringParser.parseStringsFile(RES_EASY_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        parsedStringModel
                .pluralStringSet
                .pluralString[BASE_LANGUAGE_ALL_KEYS]
                ?.pluralStringValue
                ?.forEach {
            assertEquals(
                    EXPECTED_ALL_PLURAL_ITEMS_COUNT,
                    it.value.pluralsMap.size,
                    "Items in plural collection is not equal to expected"
            )
        }
    }

    @Test
    fun `languages with missing keys should have integrity with base language`() {
        val parsedStringsModel =
                stringParser.parseStringsFile(RES_EASY_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        val baseLangSingleStrings =
                parsedStringsModel
                        .pluralStringSet
                        .pluralString[BASE_LANGUAGE_ALL_KEYS]
        val missingKeysLangSingleStrings =
                parsedStringsModel
                        .pluralStringSet
                        .pluralString[LANGUAGE_MISSING_KEYS]

        val commonSubsetSize =
                baseLangSingleStrings
                        ?.pluralStringValue
                        ?.filterKeys { missingKeysLangSingleStrings
                                ?.pluralStringValue
                                ?.keys
                                ?.contains(it) == true
                        }
                        ?.size
        assertEquals(
                EXPECTED_MISSING_PLURAL_ITEMS_COUNT,
                commonSubsetSize,
                "Not all missing keys language keys are present in base language with all keys"
        )
    }


    companion object {
        private const val RES_EASY_BASE_DIR_PATH = "src/test/res/parsing/strings/res-ios"
        private const val EXPECTED_LANGUAGES_COUNT = 2
        private const val EXPECTED_PLURAL_COLLECTION_COUNT = 3
        private const val EXPECTED_ALL_PLURAL_ITEMS_COUNT = 6
        private const val EXPECTED_MISSING_PLURAL_ITEMS_COUNT = 1
        private const val BASE_LANGUAGE_ALL_KEYS = "en"
        private const val LANGUAGE_MISSING_KEYS = "pl"
    }
}