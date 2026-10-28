package com.tec.paises

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tec.paises.ui.PaisesViewModel
import com.tec.paises.ui.screens.BanderaScreen
import com.tec.paises.ui.screens.PaisesScreen
import com.tec.paises.ui.theme.PaisesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaisesTheme {
                // Un solo ViewModel compartido por las dos pantallas:
                // la lista ya descargada se reutiliza en la pantalla de la bandera.
                val viewModel: PaisesViewModel = viewModel()
                val navegacion = rememberNavController()

                NavHost(navController = navegacion, startDestination = "paises") {
                    composable("paises") {
                        PaisesScreen(
                            viewModel = viewModel,
                            alSeleccionarPais = { pais ->
                                navegacion.navigate("bandera/${pais.codigos.alpha2}")
                            }
                        )
                    }
                    composable("bandera/{codigo}") { entrada ->
                        val codigo = entrada.arguments?.getString("codigo").orEmpty()
                        BanderaScreen(
                            pais = viewModel.buscarPais(codigo),
                            alVolver = { navegacion.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
