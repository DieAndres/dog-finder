package com.example.dogfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.layout.ContentScale

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
                            title = { Text("Dog Finder", color = Color.White) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            navigationIcon = {
                                if (currentRoute?.startsWith("detalle") == true || currentRoute == "favoritos") {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Atrás",
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            actions = {
                                if (currentRoute != "favoritos") {
                                    IconButton(onClick = { navController.navigate("favoritos") }) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Ver Favoritos",
                                            tint = Color.White
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
                            composable("favoritos") {
                                FavoritesScreen(dogViewModel = dogViewModel)
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
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onBreedClick(raza) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = raza.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun BreedDetailScreen(raza: String, dogViewModel: DogViewModel) {
    val imagenes = dogViewModel.breedImages.value
    val isLoading = dogViewModel.isLoading.value
    val context = LocalContext.current 

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
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(imagenes) { url ->
                Card(
                    modifier = Modifier
                        .padding(6.dp)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box {
                        // La foto del perro
                        AsyncImage(
                            model = url,
                            contentDescription = "Perro $raza",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(36.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            IconButton(
                                onClick = {
                                    dogViewModel.toggleFavorite(url, raza)
                                    Toast.makeText(context, "¡Añadido a favoritos!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Favorito",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(dogViewModel: DogViewModel) {
    val favoritos = dogViewModel.favorites.value
    val context = LocalContext.current

    if (favoritos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no tienes perros favoritos")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(favoritos) { perro ->
                Card(
                    modifier = Modifier
                        .padding(6.dp)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box {
                        AsyncImage(
                            model = perro.imageUrl,
                            contentDescription = "Perro favorito",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(36.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            IconButton(
                                onClick = {
                                    dogViewModel.toggleFavorite(perro.imageUrl, perro.breed)
                                    // El mensaje ahora es más genérico o podrías manejarlo según el estado
                                    Toast.makeText(context, "Lista de favoritos actualizada", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Favorito",
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
