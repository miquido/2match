package com.miquido.stringstranslator.task

import com.miquido.stringstranslator.download.FileDownloadService
import okhttp3.ResponseBody
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.File
import java.io.IOException

class SpreadsheetDownloaderTask(input: String, output: String) : InputOutputTask(input, output), KoinComponent {

    private val fileDownloadService: FileDownloadService by inject()
    private val logger: Logger by inject()

    override fun start() {
        logger.info("Downloading spreadsheet from $input... ")
        try {
            fileDownloadService.downloadFile(input).execute()?.let { response ->
                if (response.isSuccessful) {
                    logger.info("Download complete")
                    response.body()?.let { responseBody ->
                        if (!writeResponseBodyToDisk(responseBody)) logger.error("Failed to write file to disk")
                    }
                } else {
                    logger.error("Download failed ${response.errorBody()}, code: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            File(output).outputStream().use { output ->
                body.byteStream().use { input ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: IOException) {
            logger.error("Error during writing file to disk", e)
            false
        }
    }

    companion object {
        // FileDownloadService takes absolute URL as input (spreadsheets can be downloaded from GDocs, OneDrive, etc.
        // so no baseUrl. Unfortunately baseUrl is required by Retrofit so we have to give it something during creation.
        // This URL is always overridden by absolute path passed to FileDownloaderService.
        const val BASE_URL = "https://overridden.by.dynamic.absolute.url.in.service.but.required.by.retrofit"
    }
}

