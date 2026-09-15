package com.dicoding.moviecatalogue.core.data

import com.dicoding.moviecatalogue.core.data.source.local.LocalDataSource
import com.dicoding.moviecatalogue.core.data.source.remote.RemoteDataSource
import com.dicoding.moviecatalogue.core.domain.model.Movie
import com.dicoding.moviecatalogue.core.domain.repository.IMovieRepository
import com.dicoding.moviecatalogue.core.utils.DataMapper
import com.dicoding.moviecatalogue.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MovieRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : IMovieRepository {

    override fun getPopularMovies(): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        val localData = localDataSource.getAllMovies().first()
        if (localData.isNotEmpty()) {
            emitAll(
                localDataSource.getAllMovies().map { entities ->
                    Resource.Success(DataMapper.mapEntitiesToDomain(entities))
                }
            )
        }
        remoteDataSource.getPopularMovies().collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val entities = DataMapper.mapResponsesToEntities(resource.data ?: emptyList())
                    localDataSource.insertMovies(entities)
                    emitAll(
                        localDataSource.getAllMovies().map { storedEntities ->
                            Resource.Success(DataMapper.mapEntitiesToDomain(storedEntities))
                        }
                    )
                }
                is Resource.Error -> {
                    if (localData.isEmpty()) {
                        emit(Resource.Error(resource.message ?: "An error occurred"))
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    override fun getMovieDetail(movieId: Int): Flow<Resource<Movie>> = flow {
        emit(Resource.Loading())
        val localMovie = localDataSource.getMovieById(movieId).first()
        if (localMovie != null) {
            emitAll(
                localDataSource.getMovieById(movieId).map { entity ->
                    Resource.Success(DataMapper.mapEntityToDomain(entity!!))
                }
            )
        } else {
            remoteDataSource.getMovieDetail(movieId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { response ->
                            val entity = DataMapper.mapResponseToEntity(response)
                            localDataSource.insertMovie(entity)
                            emitAll(
                                localDataSource.getMovieById(movieId).map { storedEntity ->
                                    Resource.Success(DataMapper.mapEntityToDomain(storedEntity!!))
                                }
                            )
                        }
                    }
                    is Resource.Error -> emit(Resource.Error(resource.message ?: "An error occurred"))
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    override fun searchMovies(query: String): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        remoteDataSource.searchMovies(query).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val entities = DataMapper.mapResponsesToEntities(resource.data ?: emptyList())
                    // Insert search results to local so they can be managed
                    localDataSource.insertMovies(entities)
                    emitAll(
                        localDataSource.searchMovies(query).map { storedEntities ->
                            Resource.Success(DataMapper.mapEntitiesToDomain(storedEntities))
                        }
                    )
                }
                is Resource.Error -> emit(Resource.Error(resource.message ?: "An error occurred"))
                is Resource.Loading -> Unit
            }
        }
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> =
        localDataSource.getFavoriteMovies().map { entities ->
            DataMapper.mapEntitiesToDomain(entities)
        }

    override suspend fun setFavoriteMovie(movie: Movie, isFavorite: Boolean) {
        localDataSource.setFavoriteMovie(movie.id, isFavorite)
    }
}
