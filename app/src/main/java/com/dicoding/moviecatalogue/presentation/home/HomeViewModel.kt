package com.dicoding.moviecatalogue.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.moviecatalogue.core.domain.usecase.MovieUseCase
import com.dicoding.moviecatalogue.core.ui.toMovieItems
import com.dicoding.moviecatalogue.core.utils.Resource
import kotlinx.coroutines.flow.map

class HomeViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {

    val movies = movieUseCase.getPopularMovies()
        .map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data?.toMovieItems() ?: emptyList())
                is Resource.Error -> Resource.Error(resource.message ?: "Error", emptyList())
                is Resource.Loading -> Resource.Loading(resource.data?.toMovieItems())
            }
        }
        .asLiveData()
}
