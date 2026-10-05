package com.example.mymovieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val showIntro = IntroManager.shouldShowIntro(this)

        setContent {
            MaterialTheme {
                Surface {
                    var showIntroState by remember { mutableStateOf(showIntro) }

                    if (showIntroState) {
                        IntroScreen(onContinue = { showIntroState = false })
                    } else {
                        MovieListScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MovieListScreen(viewModel: MovieViewModel = viewModel()) {
    // Состояние поискового запроса
    var searchQuery by remember { mutableStateOf("") }

    // Получаем поток фильмов в зависимости от поиска
    val moviesFlow = remember(searchQuery) {
        viewModel.getMoviesFlow(searchQuery)
    }
    val movies by moviesFlow.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        // Поле поиска (пункт 4 задания)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск фильмов") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Список фильмов
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            items(movies) { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Рейтинг: ${movie.voteAverage}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = movie.overview,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3
                        )
                    }
                }
            }
        }
    }
}