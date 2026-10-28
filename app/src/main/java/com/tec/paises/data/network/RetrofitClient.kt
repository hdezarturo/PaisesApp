package com.tec.paises.data.network

import com.tec.paises.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Declaración de objeto (singleton) con el objeto Retrofit y la instancia del API.
 * Se crea una sola vez (lazy) y se reutiliza en toda la app.
 */
object RetrofitClient {

    private const val URL_BASE = "https://api.restcountries.com/"

    /** Agrega a cada petición el encabezado "Authorization: Bearer <llave>". */
    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { cadena ->
                val peticion = cadena.request().newBuilder()
                    .header("Authorization", "Bearer ${BuildConfig.RESTCOUNTRIES_API_KEY}")
                    .build()
                cadena.proceed(peticion)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: PaisesApiService by lazy {
        retrofit.create(PaisesApiService::class.java)
    }
}
