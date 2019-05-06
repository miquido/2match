package com.miquido.stringstranslator.di

import org.koin.dsl.module.module
import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger
import org.slf4j.impl.SimpleLoggerFactory

val verboseLoggerModule = module {
    single<Logger> { SimpleLoggerFactory().getLogger("2match") }
}

val emptyLoggerModule = module {
    single<Logger> { NOPLogger.NOP_LOGGER }
}