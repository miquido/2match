package com.miquido.stringstranslator.task.conversion

import com.miquido.stringstranslator.mapper.mapToPluralStringsLocalModel
import com.miquido.stringstranslator.mapper.mapToSingleStringsLocalModel
import com.miquido.stringstranslator.model.configuration.Platform
import com.miquido.stringstranslator.parsing.spreadsheet.SpreadsheetParser
import com.miquido.stringstranslator.stringwriter.StringWriterFactory
import com.miquido.stringstranslator.task.InputOutputTask
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger

class FromSpreadsheetConversionTask(
        input: String,
        output: String,
        private val platform: Platform,
        private val baseLangCode: String
) : InputOutputTask(input, output), KoinComponent {

    private val spreadsheetParser: SpreadsheetParser by inject()
    private val stringWriterFactory: StringWriterFactory by inject()
    private val logger: Logger by inject()

    override fun start() {
        logger.info("Converting $input to $output")
        val rawTranslations = spreadsheetParser.parseXlsFile(input)
        val platform = platform
        val singleStringsTranslations = rawTranslations.stringsSingle.mapToSingleStringsLocalModel(platform, logger)
        val pluralStringsTranslations = rawTranslations.stringsPlural.mapToPluralStringsLocalModel(platform, logger)

        val stringsWriter = stringWriterFactory.getStringWriter(platform, baseLangCode)
        stringsWriter.writeSingleStringsDataToFile(singleStringsTranslations, output)
        stringsWriter.writePluralStringsDataToFile(pluralStringsTranslations, output)
    }
}

