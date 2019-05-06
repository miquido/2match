package com.miquido.stringstranslator

import com.miquido.stringstranslator.cli.parseConfigFromCliArgs
import com.miquido.stringstranslator.di.networkModule
import com.miquido.stringstranslator.di.parserModule
import com.miquido.stringstranslator.di.writerModule
import com.xenomachina.argparser.mainBody
import org.koin.log.EmptyLogger
import org.koin.standalone.StandAloneContext.startKoin

fun main(args: Array<String>) = mainBody {
    startKoin(diModules, logger = EmptyLogger())
    ConversionStrategyFactory().getStrategy(parseConfigFromCliArgs(args)).forEach { it.start() }
    System.exit(0)
}

val diModules = listOf(networkModule, parserModule, writerModule)