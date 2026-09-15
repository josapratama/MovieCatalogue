package com.dicoding.moviecatalogue.core.data

import com.dicoding.moviecatalogue.core.data.source.local.LocalDataSource
import com.dicoding.moviecatalogue.core.data.source.local.entity.MovieEntity
import com.dicoding.moviecatalogue.core.data.source.remote.RemoteDataSource
import com.dicoding.moviecatalogue.core.data.source.remote.response.MovieResponse
import com.dicoding.moviecatalogue.core.utils.Resource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class MovieRepositoryTest {

    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var repository: MovieRepository

    // ── Fixtures ─────────────────────────────────────────────────────────────────

    private val dummyEntity = MovieEntity(
        id = 1,
        title = "Dune: Part Two",
        overview = "Follow the mythic journey of Paul Atreides.",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        voteAverage = 8.4,
        voteCount = 5120,
        releaseDate = "2024-02-29",
        originalLanguage = "en",
        popularity = 3421.5,
        isFavorite = false
    )

    private val dummyResponse = MovieResponse(
        id = 1,
        title = "Dune: Part Two",
        overview = "Follow the mythic journey of Paul Atreides.",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        voteAverage = 8.4,
        voteCount = 5120,
        releaseDate = "2024-02-29",
        originalLanguage = "en",
        genreIds = listOf(878),
        popularity = 3421.5
    )

    @Before
    fun setUp() {
        remoteDataSource = mock(RemoteDataSource::class.java)
        localDataSource = mock(LocalDataSource::class.java)
        repository = MovieRepository(remoteDataSource, localDataSource)
    }

    // ── getPopularMovies — cache-first path ──────────────────────────────────────

    @Test
    fun `getPopularMovies serves from local cache when cache is non-empty`() = runTest {
        val cachedEntities = listOf(dummyEntity)
        // First emission returns non-empty list (cache hit)
        `when`(localDataSource.getAllMovies())
            .thenReturn(flowOf(cachedEntities))
        `when`(remoteDataSource.getPopularMovies())
            .thenReturn(flowOf(Resource.Success(listOf(dummyResponse))))
        `when`(localDataSource.getAllMovies())
            .thenReturn(flowOf(cachedEntities))

        val results = repository.getPopularMovies().toList()

        // First emit should be Loading
        assertTrue(results[0] is Resource.Loading)
        // Should contain a Success with mapped domain model
        val successStates = results.filterIsInstance<Resource.Success<*>>()
        assertTrue(successStates.isNotEmpty())
        val movies = successStates.last().data as List<*>
        assertEquals(1, movies.size)
    }

    @Test
    fun `getPopularMovies emits Loading as first state`() = runTest {
        `when`(localDataSource.getAllMovies())
            .thenReturn(flowOf(emptyList()))
        `when`(remoteDataSource.getPopularMovies())
            .thenReturn(flowOf(Resource.Error("Network error")))

        val results = repository.getPopularMovies().toList()

        assertTrue(results.first() is Resource.Loading)
    }

    @Test
    fun `getPopularMovies emits Error when cache is empty and remote fails`() = runTest {
        `when`(localDataSource.getAllMovies())
            .thenReturn(flowOf(emptyList()))
        `when`(remoteDataSource.getPopularMovies())
            .thenReturn(flowOf(Resource.Error("Network error")))

        val results = repository.getPopularMovies().toList()

        assertTrue(results.any { it is Resource.Error })
        val error = results.filterIsInstance<Resource.Error<*>>().first()
        assertEquals("Network error", error.message)
    }

    // ── getMovieDetail ───────────────────────────────────────────────────────────

    @Test
    fun `getMovieDetail serves from local cache when entity exists`() = runTest {
        `when`(localDataSource.getMovieById(1))
            .thenReturn(flowOf(dummyEntity))

        val results = repository.getMovieDetail(1).toList()

        assertTrue(results.first() is Resource.Loading)
        val successStates = results.filterIsInstance<Resource.Success<*>>()
        assertTrue(successStates.isNotEmpty())
        assertEquals(1, (successStates.last().data as com.dicoding.moviecatalogue.core.domain.model.Movie).id)
    }

    @Test
    fun `getMovieDetail fetches from remote when local cache is empty`() = runTest {
        `when`(localDataSource.getMovieById(1))
            .thenReturn(flowOf(null), flowOf(dummyEntity))
        `when`(remoteDataSource.getMovieDetail(1))
            .thenReturn(flowOf(Resource.Success(dummyResponse)))

        val results = repository.getMovieDetail(1).toList()

        assertTrue(results.first() is Resource.Loading)
        verify(remoteDataSource).getMovieDetail(1)
    }

    @Test
    fun `getMovieDetail emits Error when remote fails and local is empty`() = runTest {
        `when`(localDataSource.getMovieById(99))
            .thenReturn(flowOf(null))
        `when`(remoteDataSource.getMovieDetail(99))
            .thenReturn(flowOf(Resource.Error("Movie not found")))

        val results = repository.getMovieDetail(99).toList()

        assertTrue(results.any { it is Resource.Error })
        val error = results.filterIsInstance<Resource.Error<*>>().first()
        assertEquals("Movie not found", error.message)
    }

    // ── getFavoriteMovies ────────────────────────────────────────────────────────

    @Test
    fun `getFavoriteMovies returns mapped domain models from local source`() = runTest {
        val favoriteEntity = dummyEntity.copy(isFavorite = true)
        `when`(localDataSource.getFavoriteMovies())
            .thenReturn(flowOf(listOf(favoriteEntity)))

        val results = repository.getFavoriteMovies().toList()

        assertEquals(1, results[0].size)
        assertTrue(results[0][0].isFavorite)
        verify(localDataSource).getFavoriteMovies()
    }

    @Test
    fun `getFavoriteMovies returns empty list when no favorites exist`() = runTest {
        `when`(localDataSource.getFavoriteMovies()).thenReturn(flowOf(emptyList()))

        val results = repository.getFavoriteMovies().toList()

        assertEquals(0, results[0].size)
    }

    // ── setFavoriteMovie ─────────────────────────────────────────────────────────

    @Test
    fun `setFavoriteMovie calls localDataSource with correct id and true flag`() = runTest {
        val movie = com.dicoding.moviecatalogue.core.domain.model.Movie(
            id = 1, title = "Dune: Part Two", overview = "", posterPath = "",
            backdropPath = "", voteAverage = 8.4, voteCount = 5120,
            releaseDate = "2024-02-29", originalLanguage = "en",
            popularity = 3421.5, isFavorite = false
        )
        repository.setFavoriteMovie(movie, true)
        verify(localDataSource).setFavoriteMovie(1, true)
    }

    @Test
    fun `setFavoriteMovie calls localDataSource with correct id and false flag`() = runTest {
        val movie = com.dicoding.moviecatalogue.core.domain.model.Movie(
            id = 5, title = "Interstellar", overview = "", posterPath = "",
            backdropPath = "", voteAverage = 8.6, voteCount = 1800,
            releaseDate = "2014-11-07", originalLanguage = "en",
            popularity = 1200.0, isFavorite = true
        )
        repository.setFavoriteMovie(movie, false)
        verify(localDataSource).setFavoriteMovie(5, false)
    }
}
