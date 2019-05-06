package com.miquido.stringstranslator.config

import com.miquido.stringstranslator.model.configuration.Mode
import java.util.*

class ConfigProperties : Properties() {

    fun validate(mode: Mode): ConfigValidationResult {
        val propertiesValidationResults = mutableListOf<ConfigValidationResult>()
        val requiredPropertiesForMode = when (mode) {
            Mode.TO_SPREADSHEET -> PROPERTIES_FOR_TO_SPREADSHEET_MODE
            Mode.FROM_SPREADSHEET -> PROPERTIES_FOR_FROM_SPREADSHEET_MODE
        }
        requiredPropertiesForMode
                .mapTo(propertiesValidationResults) { it.validate(getProperty(it.key)) }

        return if (propertiesValidationResults.none { it is ConfigError }) {
            ConfigValid
        } else {
            ConfigError(propertiesValidationResults
                    .filterIsInstance<ConfigError>()
                    .flatMap { it.errorMessages })
        }
    }

    companion object {
        private const val CONFIG_FILE_REGEX = "(.|\\s)*\\S(.|\\s)*"

        val PROPERTY_PLATFORM = ConfigProperty(
                Pair("PLATFORM", setOf("android", "ios", "web"))
        )
        val PROPERTY_OUTPUT_SPREADSHEET_FILE_PATH = ConfigProperty(
                Pair("OUTPUT_SPREADSHEET_FILE_PATH", setOf(CONFIG_FILE_REGEX))
        )
        val PROPERTY_RES_DIR_PATH = ConfigProperty(
                Pair("RES_DIR_PATH", setOf(CONFIG_FILE_REGEX))
        )
        val PROPERTY_INPUT_SPREADSHEET_XLSX_DOWNLOAD_URL = ConfigProperty(
                Pair("INPUT_SPREADSHEET_XLSX_DOWNLOAD_URL", setOf(CONFIG_FILE_REGEX))
        )
        val PROPERTY_BASE_LANGUAGE_CODE = ConfigProperty(
                Pair("BASE_LANGUAGE_CODE", setOf(CONFIG_FILE_REGEX))
        )

        val PROPERTIES_FOR_TO_SPREADSHEET_MODE = listOf(
                PROPERTY_PLATFORM, PROPERTY_OUTPUT_SPREADSHEET_FILE_PATH, PROPERTY_RES_DIR_PATH
        )
        val PROPERTIES_FOR_FROM_SPREADSHEET_MODE = listOf(
                PROPERTY_PLATFORM, PROPERTY_INPUT_SPREADSHEET_XLSX_DOWNLOAD_URL,
                PROPERTY_RES_DIR_PATH, PROPERTY_BASE_LANGUAGE_CODE
        )
    }
}

class ConfigProperty(private val keySupportedValues: Pair<String, Set<String>>) : HashMap<String, HashSet<String>>() {
    val key: String
        get() = keySupportedValues.first

    fun validate(propertyConfigurationValue: String?): ConfigValidationResult {
        if (propertyConfigurationValue == null || propertyConfigurationValue.isBlank()) {
            return ConfigError(listOf("${keySupportedValues.first} property not found"))
        }
        keySupportedValues.second
                .firstOrNull { propertyConfigurationValue.matches(Regex(it, RegexOption.IGNORE_CASE)) }
                ?: return ConfigError(
                        listOf("${keySupportedValues.first} property value *$propertyConfigurationValue* incorrect. " +
                                "Use: ${keySupportedValues.second}")
                )
        return ConfigValid
    }
}