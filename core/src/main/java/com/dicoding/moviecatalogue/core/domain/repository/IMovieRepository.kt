package com.dicoding.moviecatalogue.core.domain.repository

import com.dicoding.moviecatalogue.core.domain.model.Movie
import com.dicoding.moviecatalogue.core.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defined in the domain layer.
 * Implementation lives in the data layer (core module).
 */
interface IMovieRepository {

    fun getPopularMovies(): Flow<Resource<List<Movie>>>

    fun getMovieDetail(movieId: Int): Flow<Resource<Movie>>

    fun searchMovies(query: String): Flow<Resource<List<Movie>>>

    fun getFavoriteMovies(): Flow<List<Movie>>

    suspend fun setFavoriteMovie(movie: Movie, isFavorite: Boolean)
}
