package com.tec.paises.data.network

import com.tec.paises.data.model.RespuestaPaises
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * De acuerdo a que la versión de la api en el reto ya esta obsoleta usamos
 * la interfaz que modela el endpoint "List" de la Countries API v5.
 * Documentación: https://restcountries.com/docs/countries
 *

 */
interface PaisesApiService {

    @GET("countries/v5")
    suspend fun obtenerPaises(
        /** Tamaño de página. Máximo 100 en el plan gratuito. */
        @Query("limit") limite: Int = TAMANO_PAGINA,
        /** Cuántos registros saltar (para pedir la siguiente página). */
        @Query("offset") desplazamiento: Int = 0,
        /** Pide al servidor SOLO los campos que usa la app (respuesta más ligera). */
        @Query("response_fields") campos: String = CAMPOS
    ): RespuestaPaises

    companion object {
        const val TAMANO_PAGINA = 100
        const val CAMPOS = "names.common,codes.alpha_2,flag.url_png,population"
    }
}
