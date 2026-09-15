package com.dicoding.moviecatalogue.core.domain

import com.dicoding.moviecatalogue.core.domain.model.Movie
import com.dicoding.moviecatalogue.core.domain.repository.IMovieRepository
import com.dicoding.moviecatalogue.core.domain.usecase.MovieInteractor
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

class MovieInteractorTest {

    private lateinit var repository: IMovieRepository
    private lateinit var interactor: MovieInteractor

    private val dummyMovie = Movie(
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

    @Before
    fun setUp() {
        repository = mock(IMovieRepository::class.java)
        interactor = MovieInteractor(repository)
    }

    // ── getPopularMovies ─────────────────────────────────────────────────────────

    @Test
    fun `getPopularMovies delegates to repository and returns Success`() = runTest {
        val expected = Resource.Success(listOf(dummyMovie))
        `when`(repository.getPopularMovies()).thenReturn(flowOf(expected))

        val result = interactor.getPopularMovies().toList()

        assertTrue(result[0] is Resource.Success)
        assertEquals(listOf(dummyMovie), (result[0] as Resource.Success).data)
        verify(repository).getPopularMovies()
    }

    @Test
    fun `getPopularMovies delegates to repository and returns Error`() = runTest {
        val expected = Resource.Error<List<Movie>>("Network error")
        `when`(repository.getPopularMovies()).thenReturn(flowOf(expected))

        val result = interactor.getPopularMovies().toList()

        assertTrue(result[0] is Resource.Error)
        assertEquals("Network error", (result[0] as Resource.Error).message)
    }

    @Test
    fun `getPopularMovies returns Loading state`() = runTest {
        `when`(repository.getPopularMovies()).thenReturn(flowOf(Resource.Loading()))

        val result = interactor.getPopularMovies().toList()

        assertTrue(result[0] is Resource.Loading)
    }

    @Test
    fun `getPopularMovies returns empty list on Success with no movies`() = runTest {
        `when`(repository.getPopularMovies())
            .thenReturn(flowOf(Resource.Success(emptyList())))

        val result = interactor.getPopularMovies().toList()

        assertTrue(result[0] is Resource.Success)
        assertEquals(0, (result[0] as Resource.Success).data?.size)
    }

    // ── getMovieDetail ───────────────────────────────────────────────────────────

    @Test
    fun `getMovieDetail delegates to repository with correct id`() = runTest {
        val expected = Resource.Success(dummyMovie)
        `when`(repository.getMovieDetail(1)).thenReturn(flowOf(expected))

        val result = interactor.getMovieDetail(1).toList()

        assertTrue(result[0] is Resource.Success)
        assertEquals(dummyMovie, (result[0] as Resource.Success).data)
        verify(repository).getMovieDetail(1)
    }

    @Test
    fun `getMovieDetail returns Error when repository fails`() = runTest {
        `when`(repository.getMovieDetail(99))
            .thenReturn(flowOf(Resource.Error("Movie not found")))

        val result = interactor.getMovieDetail(99).toList()

        assertTrue(result[0] is Resource.Error)
        assertEquals("Movie not found", (result[0] as Resource.Error).message)
    }

    // ── searchMovies ─────────────────────────────────────────────────────────────

    @Test
    fun `searchMovies delegates to repository with correct query`() = runTest {
        val movies = listOf(dummyMovie, dummyMovie.copy(id = 2, title = "Dune"))
        `when`(repository.searchMovies("dune"))
            .thenReturn(flowOf(Resource.Success(movies)))

        val result = interactor.searchMovies("dune").toList()

        assertTrue(result[0] is Resource.Success)
        assertEquals(2, (result[0] as Resource.Success).data?.size)
        verify(repository).searchMovies("dune")
    }

    @Test
    fun `searchMovies returns empty list for no results`() = runTest {
        `when`(repository.searchMovies("xyznotexist"))
            .thenReturn(flowOf(Resource.Success(emptyList())))

        val result = interactor.searchMovies("xyznotexist").toList()

        assertTrue(result[0] is Resource.Success)
        assertEquals(0, (result[0] as Resource.Success).data?.size)
    }

    // ── getFavoriteMovies ────────────────────────────────────────────────────────

    @Test
    fun `getFavoriteMovies delegates to repository and returns list`() = runTest {
        val favoriteMovie = dummyMovie.copy(isFavorite = true)
        `when`(repository.getFavoriteMovies())
            .thenReturn(flowOf(listOf(favoriteMovie)))

        val result = interactor.getFavoriteMovies().toList()

        assertEquals(1, result[0].size)
        assertTrue(result[0][0].isFavorite)
        verify(repository).getFavoriteMovies()
    }

    @Test
    fun `getFavoriteMovies returns empty list when no favorites`() = runTest {
        `when`(repository.getFavoriteMovies()).thenReturn(flowOf(emptyList()))

        val result = interactor.getFavoriteMovies().toList()

        assertEquals(0, result[0].size)
    }

    // ── setFavoriteMovie ─────────────────────────────────────────────────────────

    @Test
    fun `setFavoriteMovie delegates to repository with correct params when adding`() = runTest {
        interactor.setFavoriteMovie(dummyMovie, true)
        verify(repository).setFavoriteMovie(dummyMovie, true)
    }

    @Test
    fun `setFavoriteMovie delegates to repository with correct params when removing`() = runTest {
        interactor.setFavoriteMovie(dummyMovie, false)
        verify(repository).setFavoriteMovie(dummyMovie, false)
    }
}
