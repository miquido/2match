package com.miquido.stringtranslator.parsing.strings.web

import com.miquido.stringstranslator.parsing.strings.WebStringParser
import com.miquido.stringtranslator.di.testDiModules
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import kotlin.test.assertEquals

class WebPluralStringParserTest {

    private lateinit var webStringParser: WebStringParser

    @Before
    fun setup() {
        startKoin(testDiModules)
        webStringParser = WebStringParser()
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `string files model should have correct languages count`() {
        val parsedStringModel =
                webStringParser.parseStringsFile(RES_WEB_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        assertEquals(
                EXPECTED_LANGUAGES_COUNT,
                parsedStringModel.pluralStringSet.pluralString.size,
                "Languages count is not equal to expected"
        )
    }

    @Test
    fun `strings file model should have correct count of plurals collection`() {
        val parsedStringModel =
                webStringParser.parseStringsFile(RES_WEB_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
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
                webStringParser.parseStringsFile(RES_WEB_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        parsedStringModel.
                pluralStringSet.
                pluralString[BASE_LANGUAGE_ALL_KEYS]
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
    fun `plural collection should be verifying with base language and return only correct items`() {
        val parsedStringModel =
                webStringParser.parseStringsFile(RES_WEB_MISSING_BASE_DIR_PATH, BASE_LANGUAGE_ALL_KEYS)
        val baseLanguagePluralStrings =
                parsedStringModel
                        .pluralStringSet
                        .pluralString[BASE_LANGUAGE_ALL_KEYS]
                        ?.pluralStringValue
                        ?.get(PLURAL_COLLECTION_NAME)
                        ?.pluralsMap
        val missingKeysLangPluralStrings =
                parsedStringModel
                        .pluralStringSet
                        .pluralString[LANGUAGE_MISSING_KEYS]
                        ?.pluralStringValue
                        ?.get(PLURAL_COLLECTION_NAME)
                        ?.pluralsMap

        val commonSubsetSize = baseLanguagePluralStrings?.filterKeys {
            missingKeysLangPluralStrings?.keys?.contains(it) == true
        }?.size ?: 0

        val missingKeysCount = baseLanguagePluralStrings?.keys?.size?.minus(commonSubsetSize)
        assertEquals(EXPECTED_ALL_PLURAL_ITEMS_COUNT, baseLanguagePluralStrings?.keys?.size,
                "Base language does not have all required plural items")
        assertEquals(EXPECTED_MISSING_PLURAL_ITEMS_COUNT, missingKeysCount,
                "Remaining language return not correct number of plural items")
    }


    companion object {
        private const val RES_WEB_BASE_DIR_PATH = "src/test/res/parsing/strings/res-web"
        private const val RES_WEB_MISSING_BASE_DIR_PATH = "src/test/res/parsing/strings/res-web-missing"
        private const val PLURAL_COLLECTION_NAME = "plural_collection_1"
        private const val EXPECTED_LANGUAGES_COUNT = 3
        private const val EXPECTED_PLURAL_COLLECTION_COUNT = 1
        private const val EXPECTED_ALL_PLURAL_ITEMS_COUNT = 6
        private const val EXPECTED_MISSING_PLURAL_ITEMS_COUNT = 3
        private const val BASE_LANGUAGE_ALL_KEYS = "en"
        private const val LANGUAGE_MISSING_KEYS = "de"
    }

}
