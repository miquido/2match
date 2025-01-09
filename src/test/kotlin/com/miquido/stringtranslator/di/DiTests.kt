package com.miquido.stringtranslator.di

import com.miquido.stringstranslator.di.emptyLoggerModule
import com.miquido.stringstranslator.di.verboseLoggerModule
import com.miquido.stringstranslator.diModules
import com.miquido.stringstranslator.parsing.spreadsheet.StringHtmlAwareEscaper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verifyAll

class DiTests : KoinTest {

    private val parameters = injectedParameters(definition<StringHtmlAwareEscaper>(Map::class))

    @Before
    fun setup() {
        startKoin { modules(diModules) }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test koin modules`() {
        diModules.verifyAll(injections = parameters)
    }

    @Test
    fun `test koin modules with dynamic empty logger module`() {
        loadKoinModules(emptyLoggerModule)
        diModules.verifyAll(injections = parameters)
    }

    @Test
    fun `test koin modules with dynamic verbose logger module`() {
        loadKoinModules(verboseLoggerModule)
        diModules.verifyAll(injections = parameters)
    }
}
