package com.dicoding.moviecatalogue.di

import com.dicoding.moviecatalogue.core.domain.usecase.MovieInteractor
import com.dicoding.moviecatalogue.core.domain.usecase.MovieUseCase
import com.dicoding.moviecatalogue.presentation.detail.DetailViewModel
import com.dicoding.moviecatalogue.presentation.home.HomeViewModel
import com.dicoding.moviecatalogue.presentation.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<MovieUseCase> { MovieInteractor(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { SearchViewModel(get()) }
}
