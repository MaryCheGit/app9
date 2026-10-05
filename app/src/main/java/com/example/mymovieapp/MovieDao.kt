package com.example.mymovieapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY voteAverage DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun clearAll()

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' ORDER BY voteAverage DESC")
    fun searchMovies(query: String): Flow<List<MovieEntity>>
}