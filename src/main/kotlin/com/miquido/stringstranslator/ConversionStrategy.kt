package com.miquido.stringstranslator

import com.miquido.stringstranslator.model.configuration.Config
import com.miquido.stringstranslator.model.configuration.Mode
import com.miquido.stringstranslator.task.DeleteFileTask
import com.miquido.stringstranslator.task.SpreadsheetDownloaderTask
import com.miquido.stringstranslator.task.Task
import com.miquido.stringstranslator.task.conversion.FromSpreadsheetConversionTask
import com.miquido.stringstranslator.task.conversion.ToSpreadsheetConversionTask
import java.io.File

class ConversionStrategyFactory {

    fun getStrategy(config: Config): Set<Task> {
        val tmpFile = File.createTempFile(TMP_FILE_PREFIX, TMP_FILE_SUFFIX)
        return when (config.mode) {
            Mode.TO_SPREADSHEET -> setOf(
                    ToSpreadsheetConversionTask(
                            config.resDirPath,
                            config.outputExcelFilePath,
                            config.platform,
                            config.baseLanguageCode)
            )
            Mode.FROM_SPREADSHEET -> setOf(
                    SpreadsheetDownloaderTask(
                            config.inputSpreadsheetXlsxDownloadUrl,
                            tmpFile.absolutePath),
                    FromSpreadsheetConversionTask(
                            tmpFile.absolutePath,
                            config.resDirPath,
                            config.platform,
                            config.baseLanguageCode),
                    DeleteFileTask(tmpFile.absolutePath)
            )
        }
    }

    private companion object {
        private const val TMP_FILE_PREFIX = "tmp"
        private const val TMP_FILE_SUFFIX = "2match-download.xlsx"
    }
}