package com.dicoding.moviecatalogue.core.data.source.remote

import com.dicoding.moviecatalogue.core.BuildConfig
import com.dicoding.moviecatalogue.core.data.source.remote.network.ApiService
import com.dicoding.moviecatalogue.core.data.source.remote.response.MovieResponse
import com.dicoding.moviecatalogue.core.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSource(private val apiService: ApiService) {

    fun getPopularMovies(): Flow<Resource<List<MovieResponse>>> = flow {
        try {
            val response = apiService.getPopularMovies(BuildConfig.TMDB_API_KEY)
            emit(Resource.Success(response.results))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun getMovieDetail(movieId: Int): Flow<Resource<MovieResponse>> = flow {
        try {
            val response = apiService.getMovieDetail(movieId, BuildConfig.TMDB_API_KEY)
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun searchMovies(query: String): Flow<Resource<List<MovieResponse>>> = flow {
        try {
            val response = apiService.searchMovies(BuildConfig.TMDB_API_KEY, query)
            emit(Resource.Success(response.results))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
