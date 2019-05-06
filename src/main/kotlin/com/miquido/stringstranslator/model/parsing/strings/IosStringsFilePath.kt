package com.miquido.stringstranslator.model.parsing.strings

inline class IosStringsFilePath(override val value: String) : StringsFilePath {
    override fun isPluralStringsFilePath() = value.endsWith(PLURAL_FILE_PATH_SUFFIX)

    //TODO refactor getting language code from path
    override fun getLanguageCodeFromPath(defaultLanguage: String): String {
        val extractedLanguageCode = Regex(LPROJ_PATTERN)
                .find(value)
                ?.groupValues
                ?.firstOrNull()
                ?.split(LANGUAGE_CODE_DELIMITER)
                ?.firstOrNull()
                ?: throw IllegalStateException("Cannot deduce language code from path $value")

        return if (extractedLanguageCode == BASE_RESOURCES_PREFIX) defaultLanguage else extractedLanguageCode
    }

    companion object {
        private const val LPROJ_PATTERN = "[a-zA-z]+\\.lproj"
        private const val LANGUAGE_CODE_DELIMITER = "."
        private const val BASE_RESOURCES_PREFIX = "Base"
        private const val PLURAL_FILE_PATH_SUFFIX = "dict"

    }
}