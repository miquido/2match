package com.miquido.stringtranslator.parsing.spreadsheet

import com.miquido.stringstranslator.model.configuration.Android
import com.miquido.stringstranslator.parsing.spreadsheet.StringHtmlAwareEscaper
import org.junit.Test
import kotlin.test.assertTrue

class StringHtmlAwareEscaperTest {

    @Test
    fun `html aware replace should process android strings correct`() {
        val inputs = inputOutputMap.keys.toMutableList()
        val expectedOutputs = inputOutputMap.values.toList()

        inputs.forEachIndexed { i, item ->
            inputs[i] = StringHtmlAwareEscaper(Android.ANDROID_ESCAPE_SYMBOLS_MAP).escape(item)
        }

        inputs.forEachIndexed { i, input ->
            assertTrue("$input is not equal to ${expectedOutputs[i]}"
            ) { input == expectedOutputs[i] }
        }
    }

    companion object {
        private val inputOutputMap = mapOf(
                "Test" to "Test",
                "Test<" to "Test&amp;lt;",
                "Test<>" to "Test<>",
                "<u> Test \"</u>" to "<u> Test \\\"</u>",
                "<u><b> Test </b></u>" to "<u><b> Test </b></u>",
                "<a href=\"url\"> Test </a>" to "<a href=\"url\"> Test </a>"
        )
    }
}