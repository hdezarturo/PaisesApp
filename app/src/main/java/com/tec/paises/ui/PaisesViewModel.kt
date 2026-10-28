package com.tec.paises.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tec.paises.data.model.Pais
import com.tec.paises.data.network.PaisesApiService
import com.tec.paises.data.network.RetrofitClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/** Estados posibles de la pantalla de lista. */
sealed interface EstadoPaises {
    data object Cargando : EstadoPaises
    data class Exito(val paises: List<Pais>, val cargandoMas: Boolean) : EstadoPaises
    data class Error(val mensaje: String) : EstadoPaises
}

class PaisesViewModel(
    private val api: PaisesApiService = RetrofitClient.api
) : ViewModel() {

    private val _estado = MutableLiveData<EstadoPaises>(EstadoPaises.Cargando)

    /** LiveData a la que se suscribe la vista. */
    val estado: LiveData<EstadoPaises> = _estado

    private var descarga: Job? = null

    init {
        descargarPaises()
    }

    /**
     * Función que descarga los países dentro de viewModelScope.
     *
     * La API pagina los resultados (máx. 100 por página).
     */
    fun descargarPaises() {
        if (descarga?.isActive == true) return

        descarga = viewModelScope.launch {
            _estado.value = EstadoPaises.Cargando
            val acumulados = mutableListOf<Pais>()
            var desplazamiento = 0
            try {
                do {
                    val respuesta = api.obtenerPaises(desplazamiento = desplazamiento)
                    acumulados += respuesta.datos.paises
                    val hayMas = respuesta.datos.meta.hayMas && respuesta.datos.paises.isNotEmpty()
                    _estado.value = EstadoPaises.Exito(acumulados.toList(), cargandoMas = hayMas)
                    desplazamiento += respuesta.datos.paises.size
                } while (hayMas)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val mensaje = mensajeDeError(e)
                _estado.value = if (acumulados.isEmpty()) {
                    EstadoPaises.Error(mensaje)
                } else {
                    // Si ya se mostraron algunos países, se conservan.
                    EstadoPaises.Exito(acumulados.toList(), cargandoMas = false)
                }
            }
        }
    }

    /** Busca un país ya descargado por su código . */
    fun buscarPais(codigo: String): Pais? =
        (_estado.value as? EstadoPaises.Exito)?.paises?.firstOrNull { it.codigos.alpha2 == codigo }


    /**Algunos estatus de error que se mencionan en la documentación **/
    private fun mensajeDeError(e: Exception): String = when (e) {
        is IOException -> "Sin conexión a Internet. Revisa tu red e inténtalo de nuevo."
        is HttpException -> when (e.code()) {
            401 -> "Llave de API inválida. Revisa RESTCOUNTRIES_API_KEY en local.properties."
            403 -> "Cuenta sin acceso o límite mensual alcanzado (HTTP 403)."
            429 -> "Demasiadas peticiones. Espera unos segundos e inténtalo de nuevo."
            else -> "Error del servidor (HTTP ${e.code()})."
        }
        else -> "No se pudo leer la información: ${e.localizedMessage}"
    }
}
