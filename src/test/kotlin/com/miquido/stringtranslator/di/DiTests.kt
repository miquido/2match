package com.miquido.stringtranslator.di

import com.miquido.stringstranslator.di.emptyLoggerModule
import com.miquido.stringstranslator.di.verboseLoggerModule
import com.miquido.stringstranslator.diModules
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import org.koin.test.checkModules

class DiTests : KoinTest {

    @Before
    fun setup() {
        StandAloneContext.startKoin(diModules)
    }

    @After
    fun tearDown() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun `test koin modules`() {
        checkModules(diModules)
    }

    @Test
    fun `test koin modules with dynamic empty logger module`() {
        StandAloneContext.loadKoinModules(emptyLoggerModule)
        checkModules(diModules)
    }

    @Test
    fun `test koin modules with dynamic verbose logger module`() {
        StandAloneContext.loadKoinModules(verboseLoggerModule)
        checkModules(diModules)
    }
}