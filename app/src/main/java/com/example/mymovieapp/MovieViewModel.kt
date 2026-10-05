package com.example.mymovieapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val api: TmdbApiService
    private val repository: MovieRepository

    // Состояние поиска
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        // Настройка OkHttp с отключенным прокси и TLS 1.2
        val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .build()

        val client = OkHttpClient.Builder()
            .proxy(Proxy.NO_PROXY)
            .connectionSpecs(listOf(spec))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(TmdbApiService::class.java)

        val dao = AppDatabase.getDatabase(application).movieDao()
        repository = MovieRepository(dao, api)

        // При старте — загружаем данные из сети в базу
        refreshFromNetwork()
    }

    // Обновление данных из сети (с логированием)
    fun refreshFromNetwork() {
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.TMDB_API_KEY
                Log.d("TMDb_Test", "Начинаем загрузку фильмов с ключом: ${apiKey.take(5)}...")

                val response = api.getTopRatedMovies(apiKey)
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    Log.d("TMDb_Test", "Успех! Получено фильмов: ${movies.size}")

                    val entities = movies.map { movie ->
                        MovieEntity(
                            id = movie.id,
                            title = movie.title,
                            overview = movie.overview,
                            voteAverage = movie.voteAverage,
                            posterPath = movie.posterPath
                        )
                    }

                    val dao = AppDatabase.getDatabase(getApplication()).movieDao()
                    dao.clearAll()
                    dao.insertMovies(entities)
                    Log.d("TMDb_Test", "Фильмы сохранены в базу данных")
                } else {
                    Log.e("TMDb_Test", "Ошибка сервера: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("TMDb_Test", "Ошибка сети: ${e.message}")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Возвращает поток в зависимости от запроса (кэш или поиск)
    fun getMoviesFlow(query: String): Flow<List<MovieEntity>> {
        val dao = AppDatabase.getDatabase(getApplication()).movieDao()
        return if (query.isBlank()) {
            dao.getAllMovies()
        } else {
            dao.searchMovies(query)
        }
    }
}