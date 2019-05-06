package com.miquido.stringstranslator.extensions

import java.io.File

/**
 * Creates files if not exists along with path directory structure
 */
fun File.createRecursively() {
    if (!exists()) {
        parentFile.mkdirs()
        createNewFile()
    }
}