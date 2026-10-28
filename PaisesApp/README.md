# Países del mundo — App Android (Retrofit + Coil + Jetpack Compose)

Aplicación móvil que descarga la lista de países del mundo y permite consultar su bandera.
Muestra el nombre y una miniatura de la bandera en cada renglón; al tocar un país se abre una
pantalla nueva con la bandera en grande.

## API utilizada

El servicio `https://restcountries.com/v3.1/all` del enunciado está **deprecado**, por lo que el
proyecto usa la versión actual documentada en <https://restcountries.com/docs/countries>:

| | v3.1 (deprecada) | v5 (usada) |
|---|---|---|
| URL | `https://restcountries.com/v3.1/all` | `https://api.restcountries.com/countries/v5` |
| Autenticación | ninguna | `Authorization: Bearer <API_KEY>` |
| Forma de la respuesta | arreglo de países | `{ "data": { "objects": [...], "meta": {...} } }` |
| Paginación | no | `limit` (máx. 100 gratis) y `offset` |
| Filtro de campos | `?fields=` | `?response_fields=` |

Petición que hace la app:

```
GET https://api.restcountries.com/countries/v5?limit=100&offset=0&response_fields=names.common,codes.alpha_2,flag.url_png
```

### Atributos identificados (paso 1)

| Campo JSON | Clase / propiedad | Uso |
|---|---|---|
| `data.objects` | `DatosPaises.paises` | lista de países |
| `data.meta.more` | `Meta.hayMas` | saber si hay otra página |
| `names.common` | `Nombres.comun` | nombre que se muestra |
| `codes.alpha_2` | `Codigos.alpha2` | identificador único (llave de la lista y de la navegación) |
| `flag.url_png` | `Bandera.urlPng` | imagen de la bandera |

## Configurar la llave del API

1. Crea una cuenta gratuita en <https://restcountries.com/sign-up> y copia tu API key.
2. Abre (o crea) el archivo `local.properties` en la raíz del proyecto y agrega:
   ```
   RESTCOUNTRIES_API_KEY=tu_llave_aqui
   ```
3. Sincroniza Gradle y ejecuta.

`local.properties` está en `.gitignore`, así que tu llave **no** se sube a GitHub.
Sin llave, la app usa la llave de demostración `rc_live_demo`, que solo devuelve un país de
ejemplo (Canadá): sirve para probar que todo funciona, pero para el video usa tu llave.

## Estructura

```
app/src/main/java/com/tec/paises/
├── PaisesApplication.kt          ImageLoader de Coil (caché en memoria y en disco)
├── MainActivity.kt               Navegación entre lista y bandera
├── data/
│   ├── model/Pais.kt             Clases de datos con @SerializedName
│   └── network/
│       ├── PaisesApiService.kt   Interfaz Retrofit (@GET + función suspend)
│       └── RetrofitClient.kt     object con Retrofit + instancia del API
└── ui/
    ├── PaisesViewModel.kt        LiveData + descargarPaises() en viewModelScope
    ├── screens/PaisesScreen.kt   LazyColumn (equivalente a PaisesFragment)
    ├── screens/BanderaScreen.kt  Pantalla nueva con la bandera
    └── theme/Theme.kt
```

## Cómo cubre la rúbrica

- **Métodos HTTP:** el API es de solo lectura; se usa únicamente `@GET`.
- **Interfaz del API:** `PaisesApiService` con `@GET("countries/v5")`, `@Query` para `limit`,
  `offset` y `response_fields`, y función `suspend`.
- **Clases de objetos:** solo los campos usados, con `@SerializedName`; el dato complejo
  (URL de la bandera) va en su propia clase `Bandera`, igual que nombres y códigos.
  Además se pide al servidor con `response_fields` solo esos campos.
- **Retrofit y Coil de forma óptima:**
  - Retrofit con función `suspend` dentro de `viewModelScope` (no bloquea la interfaz).
  - Se muestra la primera página en cuanto llega y las demás se agregan después.
  - Coil `AsyncImage` en `LazyColumn`: solo descarga las banderas visibles.
  - Miniaturas de 160 px en la lista y 640 px solo en el detalle.
  - Caché en memoria y disco: cada bandera se descarga una vez.
  - Placeholder y crossfade mientras carga.
- **Procesamiento de datos:** se recorre `data.objects`, se lee cada país de forma individual
  (nombre, código y bandera) y se muestra en su propio renglón; `meta.more` controla la paginación.

## Requisitos

- Android Studio Ladybug (2024.2) o más reciente, JDK 17.
- minSdk 24, compileSdk 35.

## Entrega

1. **GitHub:** crea un repositorio público y sube el proyecto
   ```
   git init && git add . && git commit -m "App de países"
   git branch -M main
   git remote add origin https://github.com/<usuario>/PaisesApp.git
   git push -u origin main
   ```
2. **Carpeta en la nube:** sube este mismo proyecto (zip) a Google Drive / OneDrive y comparte la liga.
3. **Video:** graba la app (lista cargando, desplazamiento, tocar 2–3 países, regresar) y súbelo a YouTube como "No listado".
