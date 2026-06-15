package gg.grounds.modules.core

import gg.grounds.modules.ModuleDescriptor
import gg.grounds.modules.serviceKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ModuleGraphValidatorTest {
    private val service = serviceKey<String>("test.service")

    @Test
    fun `sorts dependencies before dependants`() {
        val graph =
            ModuleGraphValidator.validate(
                listOf(
                    ModuleDescriptor(id = "app", version = "1", dependsOn = setOf("core")),
                    ModuleDescriptor(id = "core", version = "1"),
                )
            )

        assertEquals(listOf("core", "app"), graph.descriptors.map { it.id })
    }

    @Test
    fun `rejects missing dependency`() {
        assertThrows(IllegalArgumentException::class.java) {
            ModuleGraphValidator.validate(
                listOf(ModuleDescriptor(id = "app", version = "1", dependsOn = setOf("core")))
            )
        }
    }

    @Test
    fun `rejects missing required service`() {
        assertThrows(IllegalArgumentException::class.java) {
            ModuleGraphValidator.validate(
                listOf(ModuleDescriptor(id = "app", version = "1", requires = setOf(service)))
            )
        }
    }

    @Test
    fun `allows required service provided by another module`() {
        val graph =
            ModuleGraphValidator.validate(
                listOf(
                    ModuleDescriptor(id = "provider", version = "1", provides = setOf(service)),
                    ModuleDescriptor(id = "consumer", version = "1", requires = setOf(service)),
                )
            )

        assertEquals(listOf("consumer", "provider"), graph.descriptors.map { it.id }.sorted())
    }

    @Test
    fun `rejects dependency cycle`() {
        assertThrows(IllegalArgumentException::class.java) {
            ModuleGraphValidator.validate(
                listOf(
                    ModuleDescriptor(id = "a", version = "1", dependsOn = setOf("b")),
                    ModuleDescriptor(id = "b", version = "1", dependsOn = setOf("a")),
                )
            )
        }
    }
}
