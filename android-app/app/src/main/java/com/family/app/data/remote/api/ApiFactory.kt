package com.family.app.data.remote.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {
    fun create(baseUrl: String, tokenProvider: () -> String?): FamilyApi {
        val authInterceptor = Interceptor { chain ->
            val token = tokenProvider()
            val requestBuilder = chain.request().newBuilder()
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            // ngrok free tier can serve a warning/interstitial page unless this header is present.
            requestBuilder.addHeader("ngrok-skip-browser-warning", "true")
            chain.proceed(requestBuilder.build())
        }

        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FamilyApi::class.java)
    }
}
