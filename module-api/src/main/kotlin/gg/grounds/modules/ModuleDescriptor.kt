package gg.grounds.modules

data class ModuleDescriptor(
    val id: String,
    val version: String,
    val dependsOn: Set<String> = emptySet(),
    val requires: Set<ServiceKey<*>> = emptySet(),
    val provides: Set<ServiceKey<*>> = emptySet(),
) {
    init {
        require(id.isNotBlank()) { "module id must not be blank" }
        require(version.isNotBlank()) { "module version must not be blank" }
        require(id !in dependsOn) { "module $id cannot depend on itself" }
    }
}
