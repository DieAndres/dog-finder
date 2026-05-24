package com.example.dogfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dogfinder.ui.theme.DogFinderTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dogfinder.viewmodel.DogViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {    val navController = rememberNavController()
            val dogViewModel: DogViewModel = viewModel()

            DogFinderTheme {
                NavHost(navController = navController, startDestination = "lista") {
                    composable("lista") {
                        BreedListScreen(
                            dogViewModel = dogViewModel,
                            onBreedClick = { raza ->
                                navController.navigate("detalle/$raza") // Navegamos con parámetro
                            }
                        )
                    }

                    composable("detalle/{razaNombre}") { backStackEntry ->
                        val nombre = backStackEntry.arguments?.getString("razaNombre")
                        BreedDetailScreen(nombre ?: "Desconocido", dogViewModel)
                    }
                }
            }
        }
    }
}


@Composable
fun BreedListScreen(
    dogViewModel: DogViewModel,    onBreedClick: (String) -> Unit
) {
    val listaDeRazas = dogViewModel.breeds.value
    val isLoading = dogViewModel.isLoading.value
    val errorMessage = dogViewModel.errorMessage.value

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage, color = Color.Red)
            }
        }
        else -> {
            LazyColumn {
                items(listaDeRazas) { raza ->
                    Text(
                        text = raza,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable { onBreedClick(raza) }
                    )
                }
            }
        }
    }
}
@Composable
fun BreedDetailScreen(raza: String, dogViewModel: DogViewModel) {
    val imagenes = dogViewModel.breedImages.value
    val isLoading = dogViewModel.isLoading.value

    LaunchedEffect(raza) {// Ejecuta la búsqueda de fotos al abrir esta pantalla o si cambia la raza (como un useEffect).
        dogViewModel.getImagesByBreed(raza)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(imagenes) { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Perro $raza",
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DogFinderTheme {
        Greeting("Android")
    }
}