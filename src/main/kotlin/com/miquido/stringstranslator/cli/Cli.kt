package com.miquido.stringstranslator.cli

import com.miquido.stringstranslator.config.ConfigError
import com.miquido.stringstranslator.config.ConfigProperties
import com.miquido.stringstranslator.config.ConfigValid
import com.miquido.stringstranslator.di.emptyLoggerModule
import com.miquido.stringstranslator.di.verboseLoggerModule
import com.miquido.stringstranslator.model.configuration.Config
import com.miquido.stringstranslator.model.configuration.Mode
import com.miquido.stringstranslator.model.configuration.PlatformFactory
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.default
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.File
import java.io.FileReader

private const val HELP_PROLOGUE = "Use this program to convert Android/iOS strings files to spreadsheet or to " +
        "convert GDoc spreadsheet to strings files"

fun parseConfigFromCliArgs(args: Array<String>): Config {
    return ArgParser(args, helpFormatter = DefaultHelpFormatter(prologue = HELP_PROLOGUE))
            .parseInto(::Cli)
            .run {
                val loggerModule = if (verbose) verboseLoggerModule else emptyLoggerModule
                StandAloneContext.loadKoinModules(loggerModule)
                parseConfigFile(config)
            }
}

class Cli(parser: ArgParser) : KoinComponent {

    val config by parser.storing(
            CONFIGURATION_OPTION,
            CONFIGURATION_OPTION_NAME,
            help = CONFIGURATION_HELP
    )

    val verbose by parser.flagging(
            VERBOSE_OPTION,
            VERBOSE_OPTION_NAME,
            help = CONFIGURATION_HELP
    )

    val mode by parser.mapping(
            TO_SPREADSHEET_OPTION_NAME to Mode.TO_SPREADSHEET,
            FROM_SPREADSHEET_VERBOSE_OPTION_NAME to Mode.FROM_SPREADSHEET,
            help = MODE_HELP
    ).default(Mode.FROM_SPREADSHEET)

    private val logger: Logger by inject()

    fun parseConfigFile(configPath: String): Config {
        val configProperties = ConfigProperties()
        configProperties.load(FileReader(File(configPath)))
        val validationResult = configProperties.validate(mode)
        when (validationResult) {
            ConfigValid -> return Config(
                    mode,
                    PlatformFactory().getPlatform(
                            configProperties.getProperty(ConfigProperties.PROPERTY_PLATFORM.key)
                    ),
                    configProperties.getProperty(
                            ConfigProperties.PROPERTY_RES_DIR_PATH.key
                    ),
                    configProperties.getProperty(
                            ConfigProperties.PROPERTY_OUTPUT_SPREADSHEET_FILE_PATH.key
                    ),
                    configProperties.getProperty(
                            ConfigProperties.PROPERTY_INPUT_SPREADSHEET_XLSX_DOWNLOAD_URL.key
                    ),
                    configProperties.getProperty(
                            ConfigProperties.PROPERTY_BASE_LANGUAGE_CODE.key
                    )
            )
            is ConfigError -> {
                logger.error(validationResult.errorMessages.joinToString(separator = System.lineSeparator()))
                throw IllegalArgumentException("Invalid configuration see error log above")
            }
        }
    }

    companion object {
        private const val CONFIGURATION_OPTION = "-c"
        private const val CONFIGURATION_OPTION_NAME = "--config"
        private const val VERBOSE_OPTION = "-v"
        private const val VERBOSE_OPTION_NAME = "--verbose"
        private const val TO_SPREADSHEET_OPTION_NAME = "--toSpreadsheet"
        private const val FROM_SPREADSHEET_VERBOSE_OPTION_NAME = "--fromSpreadsheet"
        private const val CONFIGURATION_HELP = "Relative path to configuration properties file"
        private const val MODE_HELP = "2 match mode - use toSpreadsheet to convert platform specific strings to" +
                " spreadsheet or fromSpreadsheet to convert spreadsheet to platform specific strings"
    }
}