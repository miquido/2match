package com.miquido.stringstranslator.model.parsing.strings

inline class AndroidStringsFilePath(override val value: String) : StringsFilePath {
    override fun isPluralStringsFilePath() = value.endsWith(PLURAL_FILE_PATH_SUFFIX)

    //TODO refactor getting language code from path
    override fun getLanguageCodeFromPath(defaultLanguage: String): String {
        val valuesDirNameSplitByLanguageCode = Regex(VALUES_DIRECTORY_PATTERN)
                .find(value)
                ?.groupValues
                ?.firstOrNull()
                ?.split(LANGUAGE_CODE_SEPARATOR)
                ?.map { it.replace("/", "") }

        val extractedLanguageCode = when (valuesDirNameSplitByLanguageCode?.size) {
            //base *values* dir
            1 -> ""
            //*values-XX* dir
            2 -> valuesDirNameSplitByLanguageCode[1]
            //error
            else -> null
        } ?: throw IllegalStateException("Cannot deduce language code from path $value")

        return if (extractedLanguageCode == "") defaultLanguage else extractedLanguageCode
    }

    companion object {
        private const val LANGUAGE_CODE_SEPARATOR = "-"
        private const val VALUES_DIRECTORY_PATTERN = "/values.*/"
        private const val PLURAL_FILE_PATH_SUFFIX = "plural.xml"
    }
}