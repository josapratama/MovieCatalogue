package com.dicoding.moviecatalogue.core.data.source.remote.network

import com.dicoding.moviecatalogue.core.BuildConfig
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {

    // Certificate pinning for api.themoviedb.org
    // Pins are SHA-256 hashes of the SubjectPublicKeyInfo (SPKI) in Base64 format.
    // - Pin 1: leaf certificate (*.themoviedb.org), issued by Amazon RSA 2048 M04
    // - Pin 2: intermediate CA (Amazon RSA 2048 M04) — backup pin so app keeps working
    //          when TMDB rotates their leaf certificate.
    private const val HOSTNAME = "api.themoviedb.org"
    private const val PIN_LEAF = "sha256/QfyoR20v8hyYX7L+ikLzM/euPGSDl67gFFcor/sROMs="
    private const val PIN_INTERMEDIATE = "sha256/G9LNNAql897egYsabashkzUCTEJkWBzgoEtk8X/678c="

    fun provideApiService(): ApiService {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val certificatePinner = CertificatePinner.Builder()
            .add(HOSTNAME, PIN_LEAF)
            .add(HOSTNAME, PIN_INTERMEDIATE)
            .build()

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .certificatePinner(certificatePinner)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
