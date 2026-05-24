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
/*Para los botones de volver atras*/
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
/*Para el boton de favoritos */
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.layout.padding

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val dogViewModel: DogViewModel = viewModel()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            //Logica para el boton de atras
            DogFinderTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Dog Finder") },
                            navigationIcon = {
                                if (currentRoute?.startsWith("detalle") == true) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Atrás"
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(navController = navController, startDestination = "lista") {
                            composable("lista") {
                                BreedListScreen(
                                    dogViewModel = dogViewModel,
                                    onBreedClick = { raza ->
                                        navController.navigate("detalle/$raza")
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
    val context = LocalContext.current // Lo necesitamos para el mensaje de aviso

    LaunchedEffect(raza) {
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
                Box(modifier = Modifier.padding(4.dp)) {
                    // La foto del perro
                    AsyncImage(
                        model = url,
                        contentDescription = "Perro $raza",
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Boton de favorito
                    IconButton(
                        onClick = {
                            dogViewModel.toggleFavorite(url, raza)
                            Toast.makeText(context, "¡Añadido a favoritos!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorito",
                            tint = Color.Yellow
                        )
                    }
                }
            }
        }
    }
}
