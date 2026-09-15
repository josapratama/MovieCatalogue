package com.dicoding.moviecatalogue.core.domain.usecase

import com.dicoding.moviecatalogue.core.domain.model.Movie
import com.dicoding.moviecatalogue.core.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Interface for all movie-related use cases.
 * Defined in domain layer, implemented in MovieInteractor.
 */
interface MovieUseCase {
    fun getPopularMovies(): Flow<Resource<List<Movie>>>
    fun getMovieDetail(movieId: Int): Flow<Resource<Movie>>
    fun searchMovies(query: String): Flow<Resource<List<Movie>>>
    fun getFavoriteMovies(): Flow<List<Movie>>
    suspend fun setFavoriteMovie(movie: Movie, isFavorite: Boolean)
}
