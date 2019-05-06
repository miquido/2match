package com.miquido.stringstranslator.task

import java.io.File

class DeleteFileTask(private val path: String) : Task() {
    override fun start() = File(path).let { if (it.exists()) it.deleteRecursively() }
}