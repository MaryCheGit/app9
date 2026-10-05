package com.example.mymovieapp

import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val dao: MovieDao,
    private val api: TmdbApiService
) {
    // Поток данных из базы (кэш)
    val moviesFromCache: Flow<List<MovieEntity>> = dao.getAllMovies()

    // Функция для поиска (пункт 4)
    fun searchMovies(query: String): Flow<List<MovieEntity>> = dao.searchMovies(query)

    // Загрузка из сети и сохранение в базу
    suspend fun refreshMovies(apiKey: String) {
        try {
            val response = api.getTopRatedMovies(apiKey)
            if (response.isSuccessful) {
                val movies = response.body()?.results ?: emptyList()
                val entities = movies.map { movie ->
                    MovieEntity(
                        id = movie.id,
                        title = movie.title,
                        overview = movie.overview,
                        voteAverage = movie.voteAverage,
                        posterPath = movie.posterPath
                    )
                }
                dao.clearAll()
                dao.insertMovies(entities)
            }
        } catch (e: Exception) {
            // Если сеть недоступна — просто оставляем данные из кэша
            e.printStackTrace()
        }
    }
}