package com.dicoding.moviecatalogue.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceTest {

    @Test
    fun `Resource Loading has null data by default`() {
        val resource: Resource<String> = Resource.Loading()
        assertNull(resource.data)
    }

    @Test
    fun `Resource Loading can carry partial data`() {
        val resource = Resource.Loading("partial")
        assertEquals("partial", resource.data)
    }

    @Test
    fun `Resource Success holds correct data`() {
        val resource = Resource.Success(listOf(1, 2, 3))
        assertEquals(listOf(1, 2, 3), resource.data)
    }

    @Test
    fun `Resource Error holds message and null data`() {
        val resource: Resource<String> = Resource.Error("Network error")
        assertEquals("Network error", resource.message)
        assertNull(resource.data)
    }

    @Test
    fun `Resource Error can hold fallback data`() {
        val resource = Resource.Error("Stale cache", data = "cached_data")
        assertEquals("Stale cache", resource.message)
        assertEquals("cached_data", resource.data)
    }

    @Test
    fun `Resource Success is instance of Resource Success`() {
        val resource = Resource.Success("data")
        assertEquals("data", resource.data)
        // Verify it's specifically a Success, not Loading or Error
        val isSuccess = resource is Resource.Success
        assertTrue(isSuccess)
    }

    @Test
    fun `Resource types are distinguishable via when expression`() {
        val resources: List<Resource<Int>> = listOf(
            Resource.Loading(),
            Resource.Success(42),
            Resource.Error("fail")
        )

        val labels = resources.map { r ->
            when (r) {
                is Resource.Loading -> "loading"
                is Resource.Success -> "success"
                is Resource.Error -> "error"
            }
        }

        assertEquals(listOf("loading", "success", "error"), labels)
    }
}
