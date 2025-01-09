package com.miquido.stringstranslator

import com.miquido.stringstranslator.cli.parseConfigFromCliArgs
import com.miquido.stringstranslator.di.networkModule
import com.miquido.stringstranslator.di.parserModule
import com.miquido.stringstranslator.di.writerModule
import com.xenomachina.argparser.mainBody
import org.koin.core.logger.Level
import org.koin.mp.KoinPlatform.startKoin
import kotlin.system.exitProcess

fun main(args: Array<String>): Unit = mainBody {
    startKoin(diModules, level = Level.NONE)
    ConversionStrategyFactory().getStrategy(parseConfigFromCliArgs(args)).forEach { it.start() }
    exitProcess(0)
}

val diModules = listOf(networkModule, parserModule, writerModule)
