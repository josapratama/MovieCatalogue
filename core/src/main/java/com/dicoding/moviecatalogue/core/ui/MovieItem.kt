package com.dicoding.moviecatalogue.core.ui

import com.dicoding.moviecatalogue.core.BuildConfig

/**
 * Presentation Model - used only in the UI/presentation layer.
 * Converted from Domain Model before displaying to user.
 */
data class MovieItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: String,
    val voteCount: String,
    val releaseDate: String,
    val originalLanguage: String,
    val isFavorite: Boolean
)

fun com.dicoding.moviecatalogue.core.domain.model.Movie.toMovieItem(): MovieItem =
    MovieItem(
        id = this.id,
        title = this.title,
        overview = this.overview,
        posterUrl = if (this.posterPath.isNotEmpty()) "${BuildConfig.IMAGE_BASE_URL}${this.posterPath}" else "",
        backdropUrl = if (this.backdropPath.isNotEmpty()) "${BuildConfig.IMAGE_BASE_URL}${this.backdropPath}" else "",
        rating = String.format("%.1f", this.voteAverage),
        voteCount = "${this.voteCount} votes",
        releaseDate = this.releaseDate,
        originalLanguage = this.originalLanguage.uppercase(),
        isFavorite = this.isFavorite
    )

fun List<com.dicoding.moviecatalogue.core.domain.model.Movie>.toMovieItems(): List<MovieItem> =
    this.map { it.toMovieItem() }
