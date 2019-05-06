package com.miquido.stringstranslator.model.parsing.strings

inline class WebStringsFilePath(override val value: String) : StringsFilePath {

    override fun isPluralStringsFilePath() = value.endsWith(PLURAL_FILE_PATH_SUFFIX)

    override fun getLanguageCodeFromPath(defaultLanguage: String): String {
        return Regex(VALUES_DIRECTORY_PATTERN)
                .find(value)
                ?.groupValues
                ?.firstOrNull()
                ?.replace("/", "")
                ?.split(LANGUAGE_CODE_DELIMITER)
                ?.get(1)
                ?: throw IllegalStateException("Cannot deduce language code from path $value")
    }

    companion object {
        private const val PLURAL_FILE_PATH_SUFFIX = "plural.json"
        private const val VALUES_DIRECTORY_PATTERN = "/lang-.*/"
        private const val LANGUAGE_CODE_DELIMITER = "-"
    }
}