package com.miquido.stringtranslator.cli

import com.miquido.stringstranslator.config.ConfigError
import com.miquido.stringstranslator.config.ConfigProperties
import com.miquido.stringstranslator.config.ConfigValid
import com.miquido.stringstranslator.model.configuration.Mode
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileReader

class ConfigurationValidationTest {
    private lateinit var configProperties: ConfigProperties

    @Before
    fun setup() {
        configProperties = ConfigProperties()
    }

    @Test
    fun `correct configuration should pass validation`() {
        configProperties.load(FileReader(File(CORRECT_FILE_PATH)))
        Mode.values().forEach {
            assertThat("Correct configuration did fail validation",
                    configProperties.validate(it), instanceOf(ConfigValid::class.java))
        }
    }

    @Test
    fun `incorrect configuration with bad config file should fail validation`() {
        configProperties.load(FileReader(File(BAD_FILE_FILE_PATH)))
        Mode.values().forEach {
            assertThat("Bad file configuration should fail validation",
                    configProperties.validate(it), instanceOf(ConfigError::class.java))
        }
    }

    @Test
    fun `incorrect configuration with incorrect attributes in config file should fail validation`() {
        configProperties.load(FileReader(File(INCORRECT_ATTRS_FILE_PATH)))
        Mode.values().forEach {
            assertThat("File with incorrect configuration should fail validation",
                    configProperties.validate(it), instanceOf(ConfigError::class.java))
        }
    }

    @Test
    fun `incorrect configuration with missing attributes should fail validation`() {
        configProperties.load(FileReader(File(MISSING_ATTRS_FILE_PATH)))
        Mode.values().forEach {
            assertThat("File with missing attributes should fail validation",
                    configProperties.validate(it), instanceOf(ConfigError::class.java))
        }
    }

    @Test
    fun `minimal config for toSpreadsheet mode should pass validation`() {
        configProperties.load(FileReader(File(MINIMAL_CONFIG_TO_SPREADSHEET_MODE_FILE_PATH)))
        assertThat("File with minimal config for toSpreadsheet mode should pass validation",
                configProperties.validate(Mode.TO_SPREADSHEET), instanceOf(ConfigValid::class.java))
    }

    @Test
    fun `minimal config for fromSpreadsheet mode should pass validation`() {
        configProperties.load(FileReader(File(MINIMAL_CONFIG_FROM_SPREADSHEET_MODE_FILE_PATH)))
        assertThat("File with minimal config for fromSpreadsheet mode should pass validation",
                configProperties.validate(Mode.FROM_SPREADSHEET), instanceOf(ConfigValid::class.java))
    }

    @Test
    fun `minimal config for toSpreadsheet mode should fail validation`() {
        configProperties
                .load(FileReader(
                        File(MINIMAL_CONFIG_TO_SPREADSHEET_MODE_MISSING_ATTRS_FILE_PATH)
                ))
        assertThat("File with minimal config for toSpreadsheet mode " +
                "with missing attributes should fail validation",
                configProperties.validate(Mode.TO_SPREADSHEET),
                instanceOf(ConfigError::class.java)
        )
    }

    companion object {
        private const val CORRECT_FILE_PATH =
                "src//test/res/config/correct_config.properties"
        private const val BAD_FILE_FILE_PATH =
                "src//test/res/config/bad_file.properties"
        private const val INCORRECT_ATTRS_FILE_PATH =
                "src//test/res/config/incorrect_config_incorrect_attrs.properties"
        private const val MISSING_ATTRS_FILE_PATH =
                "src//test/res/config/incorrect_config_missing_attrs.properties"
        private const val MINIMAL_CONFIG_TO_SPREADSHEET_MODE_FILE_PATH =
                "src//test/res/config/minimal_config_for_to_spreadsheet_mode.txt"
        private const val MINIMAL_CONFIG_FROM_SPREADSHEET_MODE_FILE_PATH =
                "src//test/res/config/minimal_config_for_from_spreadsheet_mode.txt"
        private const val MINIMAL_CONFIG_TO_SPREADSHEET_MODE_MISSING_ATTRS_FILE_PATH =
                "src//test/res/config/minimal_config_for_to_spreadsheet_mode_missing_attrs.txt"
    }
}