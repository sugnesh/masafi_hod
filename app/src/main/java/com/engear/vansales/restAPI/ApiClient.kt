package com.engear.vansales.restAPI

import android.content.Context
import com.engear.vansales.common.MyApplication.Companion.BASE_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object ApiClient {

    private var retrofit: Retrofit? = null

    fun getClient(context: Context): ApiInterface {

        val client = OkHttpClient.Builder()
            .connectTimeout(120000, TimeUnit.SECONDS)
            .addInterceptor(ConnectivityInterceptor(context))
            .readTimeout(120000, TimeUnit.SECONDS).build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

//        val client = getUnsafeOkHttpClient(context)
//            .newBuilder()
//            .addInterceptor(logging)
//            .addInterceptor(ConnectivityInterceptor(context))
//            .connectTimeout(120, TimeUnit.SECONDS)
//            .readTimeout(120, TimeUnit.SECONDS)
//            .build()

        if (retrofit == null) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging) // <-- this is the important line!
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }

        return retrofit!!.create(ApiInterface::class.java)
    }

/*
    fun getService(): ApiInterface? {
        if (service == null) {
            service = getClient()!!.create<ApiInterface>(ApiInterface::class.java)
        }
        return service
    }
*/

    fun getUnsafeOkHttpClient(context: Context): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


}