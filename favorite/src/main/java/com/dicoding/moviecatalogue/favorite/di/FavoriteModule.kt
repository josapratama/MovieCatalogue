package com.dicoding.moviecatalogue.favorite.di

import com.dicoding.moviecatalogue.core.domain.usecase.MovieUseCase
import com.dicoding.moviecatalogue.favorite.presentation.FavoriteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoriteModule = module {
    viewModel {
        // get() resolves MovieUseCase from the already-loaded useCaseModule
        FavoriteViewModel(get<MovieUseCase>())
    }
}
