package com.dicoding.moviecatalogue.core.di

import com.dicoding.moviecatalogue.core.data.MovieRepository
import com.dicoding.moviecatalogue.core.data.source.local.LocalDataSource
import com.dicoding.moviecatalogue.core.data.source.local.room.MovieDatabase
import com.dicoding.moviecatalogue.core.data.source.remote.RemoteDataSource
import com.dicoding.moviecatalogue.core.data.source.remote.network.ApiConfig
import com.dicoding.moviecatalogue.core.domain.repository.IMovieRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    factory { get<MovieDatabase>().movieDao() }
    single { MovieDatabase.getInstance(androidContext()) }
}

val networkModule = module {
    single { ApiConfig.provideApiService() }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    single<IMovieRepository> { MovieRepository(get(), get()) }
}
