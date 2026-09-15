package com.dicoding.moviecatalogue.core.data.source.local

import com.dicoding.moviecatalogue.core.data.source.local.entity.MovieEntity
import com.dicoding.moviecatalogue.core.data.source.local.room.MovieDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val movieDao: MovieDao) {

    fun getAllMovies(): Flow<List<MovieEntity>> = movieDao.getAllMovies()

    fun getFavoriteMovies(): Flow<List<MovieEntity>> = movieDao.getFavoriteMovies()

    fun getMovieById(movieId: Int): Flow<MovieEntity?> = movieDao.getMovieById(movieId)

    suspend fun insertMovies(movies: List<MovieEntity>) = movieDao.insertMovies(movies)

    suspend fun insertMovie(movie: MovieEntity) = movieDao.insertMovie(movie)

    suspend fun setFavoriteMovie(movieId: Int, isFavorite: Boolean) =
        movieDao.updateFavoriteStatus(movieId, isFavorite)

    fun searchMovies(query: String): Flow<List<MovieEntity>> = movieDao.searchMovies(query)
}
