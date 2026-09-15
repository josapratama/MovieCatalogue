package com.dicoding.moviecatalogue.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.moviecatalogue.core.domain.usecase.MovieUseCase
import com.dicoding.moviecatalogue.core.ui.toMovieItems
import kotlinx.coroutines.flow.map

class FavoriteViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {

    val favoriteMovies = movieUseCase.getFavoriteMovies()
        .map { movies -> movies.toMovieItems() }
        .asLiveData()
}
