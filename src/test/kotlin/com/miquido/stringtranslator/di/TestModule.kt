package com.miquido.stringtranslator.di

import com.miquido.stringstranslator.diModules
import org.koin.dsl.module.module
import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger

val testModule = module {
    single<Logger> { NOPLogger.NOP_LOGGER }
}

val testDiModules = diModules.let {
    val modules = it.toMutableList()
    modules.add(testModule)
    modules
}