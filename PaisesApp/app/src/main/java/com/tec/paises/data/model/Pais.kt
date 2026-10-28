package com.tec.paises.data.model

import com.google.gson.annotations.SerializedName



/** Raíz de la respuesta (formato JSON:API: todo viene dentro de "data"). */
data class RespuestaPaises(
    @SerializedName("data") val datos: DatosPaises
)

/** Contenido de "data": la lista de países y la información de paginación. */
data class DatosPaises(
    @SerializedName("objects") val paises: List<Pais>,
    @SerializedName("meta") val meta: Meta
)

/** Información de paginación. Se usa para saber si hay que pedir otra página. */
data class Meta(
    @SerializedName("total") val total: Int,
    @SerializedName("more") val hayMas: Boolean
)

/** Un país: nombre, código, bandera y la poblacion */
data class Pais(
    @SerializedName("names") val nombres: Nombres,
    @SerializedName("codes") val codigos: Codigos,
    @SerializedName("flag") val bandera: Bandera,
    @SerializedName("population") val poblacion: Int,
)

/** Objeto interno "names". */
data class Nombres(
    @SerializedName("common") val comun: String
)

/** Objeto interno "codes". */
data class Codigos(
    @SerializedName("alpha_2") val alpha2: String
)

/** Objeto interno "flag": contiene el URL de la imagen PNG de la bandera. */
data class Bandera(
    @SerializedName("url_png") val urlPng: String
) {
    /**
     * El API entrega la bandera a 640 px de ancho. Para el renglón de la lista basta
     * la versión de 160 px del mismo CDN: descarga ~4 veces menos datos y la lista
     * se desplaza sin retrasos. La pantalla de detalle usa [urlPng] completa.
     */
    val urlMiniatura: String
        get() = urlPng.replace("/w640/", "/w160/")
}
