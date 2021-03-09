package com.miquido.stringstranslator.parsing.spreadsheet

import com.miquido.stringstranslator.extensions.escape

class StringHtmlAwareEscape(
        private val text: String,
        private val escapeMap: Map<String, String>
) {

    fun value(): String {
        var result = text
        val valuesToReplace = text.split(HTML_TAG_REGEX)
        valuesToReplace.forEach {
            result = result.replace(it, it.escape(escapeMap))
        }
        return result
    }

    private companion object {
        private val HTML_TAG_REGEX = Regex("<([^<])*?>")
    }
}

