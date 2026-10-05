package com.example.mymovieapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru-RU",
        @Query("page") page: Int = 1
    ): Response<TopRatedResponse>
}