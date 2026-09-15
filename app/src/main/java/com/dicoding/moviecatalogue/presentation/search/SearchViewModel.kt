package com.dicoding.moviecatalogue.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.moviecatalogue.core.domain.usecase.MovieUseCase
import com.dicoding.moviecatalogue.core.ui.toMovieItems
import com.dicoding.moviecatalogue.core.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {

    val queryFlow = MutableStateFlow("")

    val searchResults = queryFlow
        .debounce(500L)
        .filter { it.isNotBlank() }
        .flatMapLatest { query ->
            movieUseCase.searchMovies(query)
                .map { resource ->
                    when (resource) {
                        is Resource.Success -> Resource.Success(resource.data?.toMovieItems() ?: emptyList())
                        is Resource.Error -> Resource.Error(resource.message ?: "Error", emptyList())
                        is Resource.Loading -> Resource.Loading()
                    }
                }
        }
        .asLiveData()

    fun setQuery(query: String) {
        queryFlow.value = query
    }
}
