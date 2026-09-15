package com.dicoding.moviecatalogue.core.utils

import com.dicoding.moviecatalogue.core.data.source.local.entity.MovieEntity
import com.dicoding.moviecatalogue.core.data.source.remote.response.MovieResponse
import com.dicoding.moviecatalogue.core.domain.model.Movie

object DataMapper {

    // Remote Response → Local Entity
    fun mapResponsesToEntities(input: List<MovieResponse>): List<MovieEntity> =
        input.map { response ->
            MovieEntity(
                id = response.id,
                title = response.title.orEmpty(),
                overview = response.overview.orEmpty(),
                posterPath = response.posterPath.orEmpty(),
                backdropPath = response.backdropPath.orEmpty(),
                voteAverage = response.voteAverage ?: 0.0,
                voteCount = response.voteCount ?: 0,
                releaseDate = response.releaseDate.orEmpty(),
                originalLanguage = response.originalLanguage.orEmpty(),
                popularity = response.popularity ?: 0.0,
                isFavorite = false
            )
        }

    // Remote Response → Local Entity (single)
    fun mapResponseToEntity(response: MovieResponse): MovieEntity =
        MovieEntity(
            id = response.id,
            title = response.title.orEmpty(),
            overview = response.overview.orEmpty(),
            posterPath = response.posterPath.orEmpty(),
            backdropPath = response.backdropPath.orEmpty(),
            voteAverage = response.voteAverage ?: 0.0,
            voteCount = response.voteCount ?: 0,
            releaseDate = response.releaseDate.orEmpty(),
            originalLanguage = response.originalLanguage.orEmpty(),
            popularity = response.popularity ?: 0.0,
            isFavorite = false
        )

    // Local Entity → Domain Model
    fun mapEntitiesToDomain(input: List<MovieEntity>): List<Movie> =
        input.map { entity -> mapEntityToDomain(entity) }

    fun mapEntityToDomain(entity: MovieEntity): Movie =
        Movie(
            id = entity.id,
            title = entity.title,
            overview = entity.overview,
            posterPath = entity.posterPath,
            backdropPath = entity.backdropPath,
            voteAverage = entity.voteAverage,
            voteCount = entity.voteCount,
            releaseDate = entity.releaseDate,
            originalLanguage = entity.originalLanguage,
            popularity = entity.popularity,
            isFavorite = entity.isFavorite
        )

    // Domain Model → Local Entity
    fun mapDomainToEntity(movie: Movie): MovieEntity =
        MovieEntity(
            id = movie.id,
            title = movie.title,
            overview = movie.overview,
            posterPath = movie.posterPath,
            backdropPath = movie.backdropPath,
            voteAverage = movie.voteAverage,
            voteCount = movie.voteCount,
            releaseDate = movie.releaseDate,
            originalLanguage = movie.originalLanguage,
            popularity = movie.popularity,
            isFavorite = movie.isFavorite
        )
}
