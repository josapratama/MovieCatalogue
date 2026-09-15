package com.dicoding.moviecatalogue.core.utils

import com.dicoding.moviecatalogue.core.data.source.local.entity.MovieEntity
import com.dicoding.moviecatalogue.core.data.source.remote.response.MovieResponse
import com.dicoding.moviecatalogue.core.domain.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DataMapperTest {

    // ── Helper builders ─────────────────────────────────────────────────────────

    private fun buildResponse(
        id: Int = 1,
        title: String? = "Dune: Part Two",
        overview: String? = "Follow the mythic journey of Paul Atreides.",
        posterPath: String? = "/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
        backdropPath: String? = "/xOMo8BRK7PfcJv9JCnx7s5hj0PX.jpg",
        voteAverage: Double? = 8.4,
        voteCount: Int? = 5120,
        releaseDate: String? = "2024-02-29",
        originalLanguage: String? = "en",
        popularity: Double? = 3421.5
    ) = MovieResponse(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = releaseDate,
        originalLanguage = originalLanguage,
        genreIds = listOf(878, 12),
        popularity = popularity
    )

    private fun buildEntity(
        id: Int = 1,
        title: String = "Dune: Part Two",
        overview: String = "Follow the mythic journey of Paul Atreides.",
        posterPath: String = "/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
        backdropPath: String = "/xOMo8BRK7PfcJv9JCnx7s5hj0PX.jpg",
        voteAverage: Double = 8.4,
        voteCount: Int = 5120,
        releaseDate: String = "2024-02-29",
        originalLanguage: String = "en",
        popularity: Double = 3421.5,
        isFavorite: Boolean = false
    ) = MovieEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = releaseDate,
        originalLanguage = originalLanguage,
        popularity = popularity,
        isFavorite = isFavorite
    )

    private fun buildDomain(
        id: Int = 1,
        title: String = "Dune: Part Two",
        overview: String = "Follow the mythic journey of Paul Atreides.",
        posterPath: String = "/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
        backdropPath: String = "/xOMo8BRK7PfcJv9JCnx7s5hj0PX.jpg",
        voteAverage: Double = 8.4,
        voteCount: Int = 5120,
        releaseDate: String = "2024-02-29",
        originalLanguage: String = "en",
        popularity: Double = 3421.5,
        isFavorite: Boolean = false
    ) = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        voteCount = voteCount,
        releaseDate = releaseDate,
        originalLanguage = originalLanguage,
        popularity = popularity,
        isFavorite = isFavorite
    )

    // ── mapResponsesToEntities ───────────────────────────────────────────────────

    @Test
    fun `mapResponsesToEntities maps all fields correctly`() {
        val responses = listOf(buildResponse())
        val entities = DataMapper.mapResponsesToEntities(responses)

        assertEquals(1, entities.size)
        val entity = entities[0]
        assertEquals(1, entity.id)
        assertEquals("Dune: Part Two", entity.title)
        assertEquals("Follow the mythic journey of Paul Atreides.", entity.overview)
        assertEquals("/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg", entity.posterPath)
        assertEquals("/xOMo8BRK7PfcJv9JCnx7s5hj0PX.jpg", entity.backdropPath)
        assertEquals(8.4, entity.voteAverage, 0.001)
        assertEquals(5120, entity.voteCount)
        assertEquals("2024-02-29", entity.releaseDate)
        assertEquals("en", entity.originalLanguage)
        assertEquals(3421.5, entity.popularity, 0.001)
        assertFalse(entity.isFavorite) // new entities default to not-favorite
    }

    @Test
    fun `mapResponsesToEntities handles empty list`() {
        val entities = DataMapper.mapResponsesToEntities(emptyList())
        assertEquals(0, entities.size)
    }

    @Test
    fun `mapResponsesToEntities handles null fields with empty string fallback`() {
        val response = buildResponse(title = null, overview = null, posterPath = null)
        val entities = DataMapper.mapResponsesToEntities(listOf(response))

        assertEquals("", entities[0].title)
        assertEquals("", entities[0].overview)
        assertEquals("", entities[0].posterPath)
    }

    @Test
    fun `mapResponsesToEntities handles null numeric fields with zero fallback`() {
        val response = buildResponse(voteAverage = null, voteCount = null, popularity = null)
        val entities = DataMapper.mapResponsesToEntities(listOf(response))

        assertEquals(0.0, entities[0].voteAverage, 0.001)
        assertEquals(0, entities[0].voteCount)
        assertEquals(0.0, entities[0].popularity, 0.001)
    }

    @Test
    fun `mapResponsesToEntities maps multiple responses preserving order`() {
        val responses = listOf(
            buildResponse(id = 10, title = "Movie A"),
            buildResponse(id = 20, title = "Movie B"),
            buildResponse(id = 30, title = "Movie C")
        )
        val entities = DataMapper.mapResponsesToEntities(responses)

        assertEquals(3, entities.size)
        assertEquals(10, entities[0].id)
        assertEquals(20, entities[1].id)
        assertEquals(30, entities[2].id)
        assertEquals("Movie A", entities[0].title)
        assertEquals("Movie B", entities[1].title)
        assertEquals("Movie C", entities[2].title)
    }

    // ── mapResponseToEntity ──────────────────────────────────────────────────────

    @Test
    fun `mapResponseToEntity maps single response correctly`() {
        val response = buildResponse(id = 42, title = "Oppenheimer")
        val entity = DataMapper.mapResponseToEntity(response)

        assertEquals(42, entity.id)
        assertEquals("Oppenheimer", entity.title)
        assertFalse(entity.isFavorite)
    }

    // ── mapEntitiesToDomain ──────────────────────────────────────────────────────

    @Test
    fun `mapEntitiesToDomain maps all fields correctly`() {
        val entities = listOf(buildEntity())
        val movies = DataMapper.mapEntitiesToDomain(entities)

        assertEquals(1, movies.size)
        val movie = movies[0]
        assertEquals(1, movie.id)
        assertEquals("Dune: Part Two", movie.title)
        assertEquals("Follow the mythic journey of Paul Atreides.", movie.overview)
        assertEquals("/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg", movie.posterPath)
        assertEquals("/xOMo8BRK7PfcJv9JCnx7s5hj0PX.jpg", movie.backdropPath)
        assertEquals(8.4, movie.voteAverage, 0.001)
        assertEquals(5120, movie.voteCount)
        assertEquals("2024-02-29", movie.releaseDate)
        assertEquals("en", movie.originalLanguage)
        assertEquals(3421.5, movie.popularity, 0.001)
        assertFalse(movie.isFavorite)
    }

    @Test
    fun `mapEntitiesToDomain preserves isFavorite flag`() {
        val favoriteEntity = buildEntity(isFavorite = true)
        val movies = DataMapper.mapEntitiesToDomain(listOf(favoriteEntity))

        assertEquals(true, movies[0].isFavorite)
    }

    @Test
    fun `mapEntitiesToDomain handles empty list`() {
        assertEquals(0, DataMapper.mapEntitiesToDomain(emptyList()).size)
    }

    // ── mapEntityToDomain ────────────────────────────────────────────────────────

    @Test
    fun `mapEntityToDomain produces equal domain model to built directly`() {
        val entity = buildEntity(id = 99, title = "Test Movie", isFavorite = true)
        val movie = DataMapper.mapEntityToDomain(entity)

        assertEquals(entity.id, movie.id)
        assertEquals(entity.title, movie.title)
        assertEquals(entity.overview, movie.overview)
        assertEquals(entity.posterPath, movie.posterPath)
        assertEquals(entity.backdropPath, movie.backdropPath)
        assertEquals(entity.voteAverage, movie.voteAverage, 0.001)
        assertEquals(entity.voteCount, movie.voteCount)
        assertEquals(entity.releaseDate, movie.releaseDate)
        assertEquals(entity.originalLanguage, movie.originalLanguage)
        assertEquals(entity.popularity, movie.popularity, 0.001)
        assertEquals(entity.isFavorite, movie.isFavorite)
    }

    // ── mapDomainToEntity ────────────────────────────────────────────────────────

    @Test
    fun `mapDomainToEntity maps all fields correctly`() {
        val movie = buildDomain(id = 55, title = "Interstellar", isFavorite = true)
        val entity = DataMapper.mapDomainToEntity(movie)

        assertEquals(55, entity.id)
        assertEquals("Interstellar", entity.title)
        assertEquals(true, entity.isFavorite)
    }

    // ── Round-trip consistency ───────────────────────────────────────────────────

    @Test
    fun `response-to-entity-to-domain round trip preserves data`() {
        val response = buildResponse(id = 7, title = "Avatar: The Way of Water")
        val entity = DataMapper.mapResponseToEntity(response)
        val movie = DataMapper.mapEntityToDomain(entity)

        assertEquals(response.id, movie.id)
        assertEquals(response.title, movie.title)
        assertEquals(response.overview, movie.overview)
        assertEquals(response.voteAverage, movie.voteAverage)
        assertEquals(response.voteCount, movie.voteCount)
    }

    @Test
    fun `domain-to-entity-to-domain round trip preserves data`() {
        val original = buildDomain(id = 11, title = "The Batman", isFavorite = true)
        val entity = DataMapper.mapDomainToEntity(original)
        val restored = DataMapper.mapEntityToDomain(entity)

        assertEquals(original, restored)
    }
}
