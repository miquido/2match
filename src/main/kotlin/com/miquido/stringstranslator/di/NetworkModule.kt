package com.miquido.stringstranslator.di

import com.miquido.stringstranslator.download.FileDownloadService
import com.miquido.stringstranslator.task.SpreadsheetDownloaderTask
import okhttp3.OkHttpClient
import org.koin.dsl.module.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private const val WEB_SERVICE_TIMEOUT: Long = 15

val networkModule = module {
    single {
        OkHttpClient().newBuilder()
                .connectTimeout(WEB_SERVICE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(WEB_SERVICE_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WEB_SERVICE_TIMEOUT, TimeUnit.SECONDS)
                .build()
    }

    single {
        Retrofit.Builder()
                .baseUrl(SpreadsheetDownloaderTask.BASE_URL)
                .client(get())
                .build()
    }

    single { get<Retrofit>().create(FileDownloadService::class.java) as FileDownloadService }
}