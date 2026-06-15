package gg.grounds.modules.core

import gg.grounds.modules.DuplicateServiceException
import gg.grounds.modules.MissingServiceException
import gg.grounds.modules.serviceKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class DefaultServiceRegistryTest {
    private val stringService = serviceKey<String>("test.string")

    @Test
    fun `registers and retrieves typed service`() {
        val registry = DefaultServiceRegistry()

        registry.register(stringService, "hello")

        assertEquals("hello", registry.require(stringService))
    }

    @Test
    fun `throws for duplicate service`() {
        val registry = DefaultServiceRegistry()
        registry.register(stringService, "one")

        assertThrows(DuplicateServiceException::class.java) {
            registry.register(stringService, "two")
        }
    }

    @Test
    fun `throws for missing required service`() {
        val registry = DefaultServiceRegistry()

        assertThrows(MissingServiceException::class.java) { registry.require(stringService) }
    }
}
