package com.dicoding.moviecatalogue.core.domain.model

/**
 * Domain Model - Pure Kotlin class, no Android or library dependencies.
 * This is the model used across domain and presentation layers.
 */
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseDate: String,
    val originalLanguage: String,
    val popularity: Double,
    val isFavorite: Boolean
)
