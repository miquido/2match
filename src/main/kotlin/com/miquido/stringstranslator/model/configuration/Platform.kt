package com.miquido.stringstranslator.model.configuration

import com.uchuhimo.collections.BiMap
import com.uchuhimo.collections.biMapOf


class PlatformFactory {
    /**
     * @return Platform implementation based on configuration string
     */
    fun getPlatform(platformString: String) = when (platformString.toLowerCase()) {
        PLATFORM_STRING_IOS -> Ios()
        PLATFORM_STRING_ANDROID -> Android()
        PLATFORM_STRING_WEB -> Web()
        else -> throw UnsupportedOperationException("Unknown platform: $platformString. " +
                "Use android | iOS")
    }

    companion object {
        private const val PLATFORM_STRING_IOS = "ios"
        private const val PLATFORM_STRING_ANDROID = "android"
        private const val PLATFORM_STRING_WEB = "web"
    }
}

sealed class Platform {

    abstract fun getName(): String

    abstract fun getSingleKeyColumnIndex(): Int

    abstract fun getPluralKeyColumnIndex(): Int

    abstract fun getSingleStringsFileName(): String

    abstract fun getPluralStringsFileName(): String

    abstract fun getEscapeMap(): BiMap<String, String>

    fun getPluralQualifierColumnIndex() = PLURAL_KEY_COLUMN_INDEX

    companion object {
        /**
         * Index of first column in spreadsheet that contain translations in single strings spreadsheet
         */
        const val FIRST_SINGLE_STRINGS_TRANSLATION_COLUMN_INDEX = 5

        /**
         * Index of first column in spreadsheet that contain translations in plural strings spreadsheet
         */
        const val FIRST_PLURAL_STRINGS_TRANSLATION_COLUMN_INDEX = 4

        private const val PLURAL_KEY_COLUMN_INDEX = 3
    }
}

class Ios : Platform() {

    override fun getName() = PLATFORM_NAME

    override fun getEscapeMap(): BiMap<String, String> = ESCAPE_SYMBOLS_MAP

    override fun getSingleStringsFileName() = SINGLE_STRINGS_FILE_NAME

    override fun getPluralStringsFileName() = PLURAL_STRINGS_FILE_NAME

    /**
     * @return Index of iOS string key column
     */
    override fun getSingleKeyColumnIndex() = KEY_COLUMN_INDEX

    override fun getPluralKeyColumnIndex() = KEY_COLUMN_INDEX

    companion object {

        /**
         * Format of iOS single string
         */
        const val SINGLE_STRING_FORMAT = "\"%s\" = \"%s\";"

        const val KEY_LOCALIZED_STRING = "NSStringLocalizedFormatKey"
        const val VALUE_LOCALIZED_STRING = "%#@value@"
        const val VALUE = "value"
        const val KEY_FORMAT_SPEC = "NSStringFormatSpecTypeKey"
        const val VALUE_FORMAT_SPEC = "NSStringPluralRuleType"
        const val KEY_FORMAT_VALUE_TYPE = "NSStringFormatValueTypeKey"
        const val VALUE_FORMAT_VALUE_TYPE = "d"

        /**
         * Map with characters that need escaping when putting them in strings
         */
        val ESCAPE_SYMBOLS_MAP = biMapOf(
                "\"" to "\\\"",
                "%s" to "%@",
                "[_]" to "\\U00A0")

        private const val KEY_COLUMN_INDEX = 0
        private const val PLATFORM_NAME = "iOS"
        private const val SINGLE_STRINGS_FILE_NAME = "Localizable.strings"
        private const val PLURAL_STRINGS_FILE_NAME = "Localizable.stringsdict"
    }
}

class Android : Platform() {

    override fun getName() = PLATFORM_NAME

    override fun getEscapeMap(): BiMap<String, String> = ANDROID_ESCAPE_SYMBOLS_MAP

    override fun getSingleStringsFileName() = SINGLE_STRINGS_FILE_NAME

    override fun getPluralStringsFileName() = PLURAL_STRINGS_FILE_NAME

    /**
     * @return Index of Android string key column
     */
    override fun getSingleKeyColumnIndex() = KEY_COLUMN_INDEX

    override fun getPluralKeyColumnIndex() = KEY_COLUMN_INDEX


    companion object {

        /**
         * Index of Android isTranslatable boolean attribute column
         */
        const val TRANSLATABLE_COLUMN_INDEX = 2

        /**
         * Index of Android isFormatted boolean attribute column
         */
        const val FORMATTED_COLUMN_INDEX = 3


        const val RESOURCES_OPEN_TAG = "<resources>"
        const val RESOURCES_CLOSE_TAG = "</resources>"
        const val SINGLE_STRING_FORMAT = "<string name=\"%s\" formatted=\"%b\" translatable=\"%b\">%s</string>"
        const val STRING_PLURAL_PARENT_TAG_START_FORMAT = "<plurals name=\"%s\">"
        const val STRING_PLURAL_PARENT_TAG_END = "</plurals>"
        const val STRING_PLURAL_ITEM_FORMAT = "<item quantity=\"%s\">%s</item>"

        /**
         * Map with characters that need escaping when putting them in strings
         */
        val ANDROID_ESCAPE_SYMBOLS_MAP = biMapOf(
                "@" to "\\@",
                "?" to "\\?",
                "<" to "&lt;",
                "&" to "&amp;",
                "'" to "\\'",
                "\"" to "\\\"",
                "[_]" to "&#160;")

        private const val KEY_COLUMN_INDEX = 1
        private const val PLATFORM_NAME = "Android"
        private const val SINGLE_STRINGS_FILE_NAME = "strings.xml"
        private const val PLURAL_STRINGS_FILE_NAME = "strings_plural.xml"
    }
}

class Web : Platform() {

    override fun getName() = PLATFORM_NAME

    /**
     * @return Index of Web string key column
     */
    override fun getSingleKeyColumnIndex() = SINGLE_KEY_COLUMN_INDEX

    override fun getPluralKeyColumnIndex() = PLURAL_KEY_COLUMN_INDEX

    override fun getSingleStringsFileName() = SINGLE_STRINGS_FILE_NAME

    override fun getPluralStringsFileName() = PLURAL_STRINGS_FILE_NAME

    override fun getEscapeMap() = WEB_ESCAPE_SYMBOLS_MAP

    companion object {
        private const val SINGLE_KEY_COLUMN_INDEX = 4
        private const val PLURAL_KEY_COLUMN_INDEX = 2
        private const val PLATFORM_NAME = "Web"
        private const val SINGLE_STRINGS_FILE_NAME = "strings.json"
        private const val PLURAL_STRINGS_FILE_NAME = "strings_plural.json"
        val WEB_ESCAPE_SYMBOLS_MAP = biMapOf(
                "[_]" to "&nbsp;")
    }
}