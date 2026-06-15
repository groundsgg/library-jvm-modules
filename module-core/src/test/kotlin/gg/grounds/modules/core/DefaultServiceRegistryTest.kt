package gg.grounds.modules.core

import gg.grounds.modules.DuplicateServiceException
import gg.grounds.modules.MissingServiceException
import gg.grounds.modules.qualifiedServiceKey
import gg.grounds.modules.register
import gg.grounds.modules.require
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class DefaultServiceRegistryTest {
    private interface GreetingService {
        fun greet(): String
    }

    private class FriendlyGreetingService : GreetingService {
        override fun greet(): String = "hello"
    }

    private class FormalGreetingService : GreetingService {
        override fun greet(): String = "good day"
    }

    @Test
    fun `registers and retrieves service by type`() {
        val registry = DefaultServiceRegistry()

        registry.register<GreetingService>(FriendlyGreetingService())

        assertEquals("hello", registry.require<GreetingService>().greet())
    }

    @Test
    fun `registers and retrieves service by class`() {
        val registry = DefaultServiceRegistry()

        registry.register(GreetingService::class, FriendlyGreetingService())

        assertEquals("hello", registry.require(GreetingService::class).greet())
    }

    @Test
    fun `throws for duplicate type service`() {
        val registry = DefaultServiceRegistry()
        registry.register<GreetingService>(FriendlyGreetingService())

        assertThrows(DuplicateServiceException::class.java) {
            registry.register<GreetingService>(FormalGreetingService())
        }
    }

    @Test
    fun `throws for missing required type service`() {
        val registry = DefaultServiceRegistry()

        assertThrows(MissingServiceException::class.java) { registry.require<GreetingService>() }
    }

    @Test
    fun `allows qualified services with the same type`() {
        val friendly = qualifiedServiceKey<GreetingService>("test.greeting.friendly")
        val formal = qualifiedServiceKey<GreetingService>("test.greeting.formal")
        val registry = DefaultServiceRegistry()

        registry.register(friendly, FriendlyGreetingService())
        registry.register(formal, FormalGreetingService())

        assertEquals("hello", registry.require(friendly).greet())
        assertEquals("good day", registry.require(formal).greet())
    }
}
