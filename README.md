# 🐶 DoggoWiki — Dog Finder

Aplicación móvil Android que permite explorar razas de perros, ver galerías de
fotos y guardar las imágenes favoritas localmente. Desarrollada como Tarea 2 del
curso de Desarrollo de Aplicaciones Móviles 2026.

---

## 📑 Índice

1. [Descripción](#descripción)
2. [Integrantes](#integrantes)
3. [API utilizada](#api-utilizada)
4. [Tecnologías y dependencias](#tecnologías-y-dependencias)
5. [Arquitectura](#arquitectura)
6. [Estructura del proyecto y clases](#estructura-del-proyecto-y-clases)
7. [Componentes Android](#componentes-android)
8. [Cómo compilar y ejecutar](#cómo-compilar-y-ejecutar)
9. [Manual de usuario](#manual-de-usuario)
10. [Tests](#tests)
11. [Fastlane (CI/CD)](#fastlane-cicd)
12. [Reportes de seguridad y calidad](#reportes-de-seguridad-y-calidad)

---

## Descripción

DoggoWiki es una app que consume la API pública **Dog CEO** para mostrar un
catálogo de razas de perros. El usuario puede:

- Buscar razas por nombre con un buscador dinámico
- Ver una galería de fotos de cada raza
- Marcar fotos como favoritas (se guardan en el dispositivo)
- Compartir imágenes con otras aplicaciones
- Descargar las fotos favoritas al almacenamiento del dispositivo
- Alternar entre modo claro y oscuro (preferencia persistente)

La app sigue las guías de **Material Design 3**.

---

## Integrantes


- Nicolas Caputto
- Damaso Tor
- Diego Koci

**Repositorio:** https://github.com/DieAndres/dog-finder

---

## API utilizada

**Dog CEO API** — https://dog.ceo/dog-api/

Es una API pública y gratuita que no requiere clave de autenticación. Endpoints
utilizados:

| Endpoint | Uso en la app |
|----------|---------------|
| `GET breeds/list/all` | Obtiene la lista completa de razas |
| `GET breed/{breed}/images` | Obtiene las fotos de una raza específica |

---

## Tecnologías y dependencias

| Categoría | Tecnología |
|-----------|------------|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | MVVM (Model–View–ViewModel) |
| Llamadas HTTP | Retrofit 2 + Gson |
| Carga de imágenes | Coil |
| Base de datos local | Room (sobre SQLite) |
| Navegación | Navigation Compose |
| Asincronía | Kotlin Coroutines |
| Tests | JUnit, MockK, Coroutines Test |
| CI/CD | Fastlane |
| Build system | Gradle 9.3.1 (Kotlin DSL) |

---

## Arquitectura

La aplicación sigue el patrón **MVVM**, que separa la app en capas con
responsabilidades bien definidas:

```
┌─────────────────────────────────────────────┐
│  VIEW (Jetpack Compose)                      │
│  Pantallas: Lista, Detalle, Favoritos        │
└───────────────────┬─────────────────────────┘
                     │  observa el estado
┌───────────────────▼─────────────────────────┐
│  VIEWMODEL (DogViewModel)                    │
│  Lógica de negocio y estado de la UI         │
└─────────┬──────────────────────┬─────────────┘
          │                      │
┌─────────▼──────────┐  ┌────────▼─────────────┐
│  API (Retrofit)    │  │  Room Database       │
│  Dog CEO API       │  │  Favoritos locales   │
└────────────────────┘  └──────────────────────┘
```

- **View:** las pantallas hechas con Compose. Solo muestran datos y reportan
  las acciones del usuario. No contienen lógica.
- **ViewModel:** contiene la lógica, mantiene el estado de la UI y sobrevive a
  los cambios de configuración (como rotar la pantalla).
- **Capa de datos:** la API para los datos remotos y Room para la persistencia
  local de favoritos.

---

## Estructura del proyecto y clases

```
app/src/main/java/com/example/dogfinder/
├── MainActivity.kt              # Activity principal + pantallas Compose
├── ThemePreferences.kt          # Persistencia de la preferencia del modo oscuro
├── data/
│   ├── api/
│   │   ├── DogApiService.kt     # Interfaz Retrofit con los endpoints
│   │   └── RetrofitInstance.kt  # Configuración de Retrofit
│   └── local/
│       ├── AppDatabase.kt       # Base de datos Room
│       ├── DogDao.kt            # Operaciones sobre la tabla de favoritos
│       └── FavoriteDog.kt       # Entidad (tabla) de favoritos
├── models/
│   ├── BreedResponse.kt         # Modelo de la respuesta de razas
│   └── DogImageResponse.kt      # Modelo de la respuesta de imágenes
├── viewmodel/
│   └── DogViewModel.kt          # ViewModel principal
├── service/
│   └── DogDownloadService.kt    # Service que descarga las fotos favoritas
├── receiver/
│   └── DownloadReceiver.kt      # BroadcastReceiver que notifica al terminar
└── ui/theme/                    # Colores, tipografía y tema Material 3
```

### Descripción de las clases principales

| Clase | Responsabilidad |
|-------|-----------------|
| `MainActivity` | Punto de entrada de la app. Configura la navegación entre las tres pantallas y contiene los composables de cada pantalla. |
| `DogApiService` | Interfaz de Retrofit que define los endpoints de la Dog CEO API. |
| `RetrofitInstance` | Objeto que crea y configura la instancia de Retrofit con la URL base. |
| `DogViewModel` | Gestiona el estado de la app: lista de razas, imágenes, favoritos, carga y errores. Hace las llamadas a la API y a la base de datos. |
| `AppDatabase` | Define la base de datos Room que almacena los favoritos. |
| `DogDao` | Define las operaciones sobre la tabla de favoritos: insertar, eliminar, listar y verificar existencia. |
| `FavoriteDog` | Entidad de Room. Representa un perro favorito (URL de imagen y raza). |
| `BreedResponse` / `DogImageResponse` | Clases de datos que modelan las respuestas JSON de la API. |
| `ThemePreferences` | Envoltorio de DataStore que guarda y lee la preferencia del modo oscuro. |
| `DogDownloadService` | Service que descarga las fotos favoritas en background. |
| `DownloadReceiver` | BroadcastReceiver que muestra una notificación cuando termina la descarga. |

### Pantallas

| Pantalla | Composable | Descripción |
|----------|-----------|-------------|
| Lista de razas | `BreedListScreen` | Muestra todas las razas con un buscador para filtrar por nombre. |
| Detalle de raza | `BreedDetailScreen` | Galería de fotos de la raza seleccionada, con opción de favorito y compartir. |
| Favoritos | `FavoritesScreen` | Galería de las fotos guardadas como favoritas, con opción de descargarlas.. |

---

## Componentes Android

| Componente | Implementación |
|------------|----------------|
| **Activity** | `MainActivity` — única Activity que aloja las pantallas de Compose. |
| **Service** | `DogDownloadService` — servicio encargado de la descarga de imágenes en segundo plano. |
| **BroadcastReceiver** | `DownloadReceiver` — escucha el broadcast que envía el Service al terminar la descarga y muestra una notificación al usuario con la cantidad de fotos descargadas. |
| **Intents** | Se usan para la navegación interna, compartir fotos (`ACTION_SEND`) e iniciar el servicio de descarga. |

> Nota: el componente Content Provider están
> contemplados como mejora futura del proyecto.

---

## Cómo compilar y ejecutar

### Requisitos previos

- **Android Studio** (versión reciente, recomendado Ladybug o superior)
- **JDK 11** o superior (Android Studio ya incluye uno)
- Conexión a internet (la app consume una API remota)

### Pasos

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/DieAndres/dog-finder.git
   ```

2. **Abrir en Android Studio:**
   - Abrir Android Studio → `Open` → seleccionar la carpeta `dog-finder`.
   - Esperar a que Gradle descargue las dependencias (la primera vez tarda
     varios minutos).

3. **Ejecutar la app:**
   - Seleccionar un emulador o conectar un dispositivo Android físico por USB
     (con la depuración USB activada).
   - Presionar el botón **Run ▶** (o `Shift + F10`).

La app se compilará e instalará automáticamente en el dispositivo.

---

## Manual de usuario

### Buscar una raza
Al abrir la app se muestra la lista completa de razas. En la parte superior hay
un campo de búsqueda: a medida que se escribe, la lista se filtra mostrando solo
las razas que coinciden con el texto.

### Ver fotos de una raza
Tocar cualquier raza de la lista abre la pantalla de detalle, donde se muestra
una galería de fotos de esa raza en una grilla de dos columnas.

### Guardar un favorito
En la pantalla de detalle, cada foto tiene un ícono de estrella en la esquina
superior derecha. Al tocarlo, la foto se guarda en favoritos. Si la foto ya
estaba guardada, al tocar la estrella se elimina de favoritos.

### Ver los favoritos
Tocando el ícono de estrella en la barra superior se accede a la pantalla de
favoritos, que muestra todas las fotos guardadas. Los favoritos se conservan
aunque se cierre la app, porque se almacenan en la base de datos local.

### Compartir una foto
Cada foto tiene un ícono de compartir en la esquina superior izquierda. Al
tocarlo se abre el menú de compartir de Android, permitiendo enviar la imagen
por WhatsApp, correo u otras aplicaciones.

### Descargar fotos de favoritos
Dentro de la pantalla de favoritos, se incluye un botón de descarga en la barra
superior. Al accionarlo, se inicia el `DogDownloadService`, que descarga
físicamente las imágenes a la carpeta privada de la app para su consulta local.

### Cambiar entre modo claro y oscuro
En la barra superior hay un ícono de sol/luna que alterna entre el modo claro y
el modo oscuro. La preferencia se guarda con DataStore y se mantiene al cerrar
y reabrir la app.

### Navegación
El botón de flecha en la barra superior permite volver a la pantalla anterior.

---

## Tests

El proyecto incluye tests unitarios ubicados en
`app/src/test/java/com/example/dogfinder/`.

`DogViewModelTest` verifica el comportamiento del ViewModel simulando las
respuestas de la API y la base de datos con MockK, sin depender de internet ni
de un dispositivo real.

### Ejecutar los tests

Desde Android Studio: clic derecho sobre la carpeta `test` → `Run Tests`.

Desde la terminal:
```bash
./gradlew testDebugUnitTest
```

---

## Fastlane (CI/CD)

El proyecto usa **Fastlane** para automatizar la ejecución de tests y la
compilación del APK.

La configuración está en `fastlane/Fastfile` e incluye un lane llamado
`entrega` que ejecuta los tests unitarios y luego compila el APK de debug.

### Ejecutar el lane

```bash
fastlane entrega
```

> Nota: el `Fastfile` define la variable `JAVA_HOME` apuntando a la instalación
> de Android Studio. Si Android Studio está instalado en otra ruta, hay que
> ajustar esa línea del archivo.

---

## Reportes de seguridad y calidad

<!--
Esta sección se completará si se incorporan las herramientas de análisis.
Detekt (SAST) genera un informe de calidad de código y OWASP Dependency Check
genera un informe de vulnerabilidades en las dependencias.
Cuando se ejecuten, los informes se guardarán y enlazarán desde aquí.
-->

_Sección pendiente — se completará si se incorpora Detekt y/o Dependency Check._

---

_Proyecto académico — Desarrollo de Aplicaciones Móviles 2026._
