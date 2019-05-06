package com.miquido.stringstranslator.model.configuration

data class Config(
        val mode: Mode,
        val platform: Platform,
        val resDirPath: String,
        val outputExcelFilePath: String,
        val inputSpreadsheetXlsxDownloadUrl: String,
        val baseLanguageCode: String
)