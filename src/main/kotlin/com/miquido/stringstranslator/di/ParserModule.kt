package com.miquido.stringstranslator.di

import com.google.gson.Gson
import com.miquido.stringstranslator.model.parsing.strings.StringsFilePathFactory
import com.miquido.stringstranslator.parsing.spreadsheet.SpreadsheetParser
import com.miquido.stringstranslator.parsing.strings.StringParserFactory
import org.koin.dsl.module.module

val parserModule = module {
    single { StringParserFactory() }
    single { SpreadsheetParser() }
    single { StringsFilePathFactory() }
    single { Gson().newBuilder().setPrettyPrinting().create() }
}