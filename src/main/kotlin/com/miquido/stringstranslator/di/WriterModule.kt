package com.miquido.stringstranslator.di

import com.miquido.stringstranslator.parsing.spreadsheet.StringHtmlAwareEscaper
import com.miquido.stringstranslator.stringwriter.StringWriterFactory
import org.koin.dsl.module.module

val writerModule = module {
    single { StringWriterFactory() }
    factory { (escapeMap: Map<String, String>) ->
        StringHtmlAwareEscaper(escapeMap)
    }
}