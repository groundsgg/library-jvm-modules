package gg.grounds.modules.core

import gg.grounds.modules.ModuleDescriptor
import gg.grounds.modules.ServiceKey

data class ModuleGraph(val descriptors: List<ModuleDescriptor>) {
    val descriptorsById: Map<String, ModuleDescriptor> = descriptors.associateBy { it.id }
}

object ModuleGraphValidator {
    fun validate(
        descriptors: Collection<ModuleDescriptor>,
        availableServices: Set<ServiceKey<*>> = emptySet(),
    ): ModuleGraph {
        val orderedDescriptors = descriptors.toList()
        require(orderedDescriptors.isNotEmpty()) { "at least one module descriptor is required" }
        ensureUniqueModuleIds(orderedDescriptors)
        ensureDependenciesExist(orderedDescriptors)
        ensureServiceContracts(orderedDescriptors, availableServices)
        return ModuleGraph(ModuleDependencySorter.sort(orderedDescriptors))
    }

    private fun ensureUniqueModuleIds(descriptors: List<ModuleDescriptor>) {
        val duplicates = descriptors.groupBy { it.id }.filterValues { it.size > 1 }.keys
        require(duplicates.isEmpty()) {
            "duplicate module ids: ${duplicates.sorted().joinToString()}"
        }
    }

    private fun ensureDependenciesExist(descriptors: List<ModuleDescriptor>) {
        val ids = descriptors.mapTo(mutableSetOf()) { it.id }
        val missing =
            descriptors.flatMap { descriptor ->
                descriptor.dependsOn.filterNot(ids::contains).map { dependency ->
                    "${descriptor.id} -> $dependency"
                }
            }
        require(missing.isEmpty()) {
            "missing module dependencies: ${missing.sorted().joinToString()}"
        }
    }

    private fun ensureServiceContracts(
        descriptors: List<ModuleDescriptor>,
        availableServices: Set<ServiceKey<*>>,
    ) {
        val providers = mutableMapOf<ServiceKey<*>, String>()
        for (descriptor in descriptors) {
            for (provided in descriptor.provides) {
                val previous = providers.putIfAbsent(provided, descriptor.id)
                require(previous == null) {
                    "service $provided is provided by both $previous and ${descriptor.id}"
                }
            }
        }

        val available = availableServices + providers.keys
        val missing =
            descriptors.flatMap { descriptor ->
                descriptor.requires.filterNot(available::contains).map { service ->
                    "${descriptor.id} -> $service"
                }
            }
        require(missing.isEmpty()) {
            "missing required services: ${missing.sorted().joinToString()}"
        }
    }
}

object ModuleDependencySorter {
    fun sort(descriptors: Collection<ModuleDescriptor>): List<ModuleDescriptor> {
        val byId = descriptors.associateBy { it.id }
        val permanent = mutableSetOf<String>()
        val temporary = mutableSetOf<String>()
        val sorted = mutableListOf<ModuleDescriptor>()

        fun visit(id: String, path: List<String>) {
            if (id in permanent) return
            require(id !in temporary) {
                "module dependency cycle: ${(path + id).joinToString(" -> ")}"
            }

            temporary.add(id)
            val descriptor = byId.getValue(id)
            for (dependency in descriptor.dependsOn.sorted()) {
                visit(dependency, path + id)
            }
            temporary.remove(id)
            permanent.add(id)
            sorted.add(descriptor)
        }

        for (descriptor in descriptors.sortedBy { it.id }) {
            visit(descriptor.id, emptyList())
        }
        return sorted
    }
}
