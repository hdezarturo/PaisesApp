package com.tec.paises.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tec.paises.data.model.Pais
import com.tec.paises.ui.EstadoPaises
import com.tec.paises.ui.PaisesViewModel

/**
 * Pantalla principal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaisesScreen(
    viewModel: PaisesViewModel,
    alSeleccionarPais: (Pais) -> Unit
) {
    val estado by viewModel.estado.observeAsState(EstadoPaises.Cargando)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Países del mundo") }) }
    ) { relleno ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
        ) {
            when (val e = estado) {
                is EstadoPaises.Cargando ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                is EstadoPaises.Error ->
                    MensajeError(
                        mensaje = e.mensaje,
                        alReintentar = viewModel::descargarPaises,
                        modifier = Modifier.align(Alignment.Center)
                    )

                is EstadoPaises.Exito ->
                    ListaPaises(
                        paises = e.paises,
                        cargandoMas = e.cargandoMas,
                        alSeleccionarPais = alSeleccionarPais
                    )
            }
        }
    }
}

@Composable
private fun ListaPaises(
    paises: List<Pais>,
    cargandoMas: Boolean,
    alSeleccionarPais: (Pais) -> Unit
) {
    // LazyColumn solo compone (y Coil solo descarga) los renglones visibles.
    LazyColumn(Modifier.fillMaxSize()) {
        items(
            items = paises,
            key = { "${it.codigos.alpha2}-${it.nombres.comun}" }
        ) { pais ->
            FilaPais(pais = pais, alPresionar = { alSeleccionarPais(pais) })
            HorizontalDivider()
        }
        if (cargandoMas) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        }
    }
}

/** Un renglón: miniatura de la bandera + nombre del país. */
@Composable
private fun FilaPais(pais: Pais, alPresionar: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = alPresionar),
        leadingContent = {
            AsyncImage(
                model = pais.bandera.urlMiniatura,
                contentDescription = "Bandera de ${pais.nombres.comun}",
                placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 56.dp, height = 38.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        },
        headlineContent = { Text(pais.nombres.comun) },
        supportingContent = { Text(pais.codigos.alpha2) }
    )
}

@Composable
private fun MensajeError(
    mensaje: String,
    alReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(mensaje, textAlign = TextAlign.Center)
        Button(onClick = alReintentar) { Text("Reintentar") }
    }
}
