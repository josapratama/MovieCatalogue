package com.dicoding.moviecatalogue.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.moviecatalogue.core.domain.model.Movie
import com.dicoding.moviecatalogue.core.domain.usecase.MovieUseCase
import com.dicoding.moviecatalogue.core.ui.MovieItem
import com.dicoding.moviecatalogue.core.ui.toMovieItem
import com.dicoding.moviecatalogue.core.utils.Resource
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DetailViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {

    private val _domainMovie = MutableLiveData<Movie?>()
    val domainMovie: LiveData<Movie?> = _domainMovie

    fun getMovieDetail(movieId: Int): LiveData<Resource<MovieItem?>> =
        movieUseCase.getMovieDetail(movieId)
            .map { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _domainMovie.postValue(resource.data)
                        Resource.Success(resource.data?.toMovieItem())
                    }
                    is Resource.Error -> Resource.Error(resource.message ?: "Error")
                    is Resource.Loading -> Resource.Loading()
                }
            }
            .asLiveData()

    fun setFavoriteMovie(isFavorite: Boolean) {
        val movie = _domainMovie.value ?: return
        viewModelScope.launch {
            movieUseCase.setFavoriteMovie(movie, isFavorite)
            _domainMovie.value = movie.copy(isFavorite = isFavorite)
        }
    }
}
