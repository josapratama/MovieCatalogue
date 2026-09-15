package com.dicoding.moviecatalogue.core.domain.usecase

import com.dicoding.moviecatalogue.core.domain.model.Movie
import com.dicoding.moviecatalogue.core.domain.repository.IMovieRepository
import com.dicoding.moviecatalogue.core.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Concrete implementation of MovieUseCase.
 * Delegates to the repository interface.
 */
class MovieInteractor(private val movieRepository: IMovieRepository) : MovieUseCase {

    override fun getPopularMovies(): Flow<Resource<List<Movie>>> =
        movieRepository.getPopularMovies()

    override fun getMovieDetail(movieId: Int): Flow<Resource<Movie>> =
        movieRepository.getMovieDetail(movieId)

    override fun searchMovies(query: String): Flow<Resource<List<Movie>>> =
        movieRepository.searchMovies(query)

    override fun getFavoriteMovies(): Flow<List<Movie>> =
        movieRepository.getFavoriteMovies()

    override suspend fun setFavoriteMovie(movie: Movie, isFavorite: Boolean) =
        movieRepository.setFavoriteMovie(movie, isFavorite)
}
