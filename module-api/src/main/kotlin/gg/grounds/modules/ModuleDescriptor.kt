package gg.grounds.modules

/**
 * Static description of one module on the application classpath.
 *
 * Descriptors are used for graph validation and startup ordering. They do not create modules and
 * they do not imply runtime classloader isolation.
 */
data class ModuleDescriptor(
    /** Stable module identifier, for example `grounds.matchmaking`. */
    val id: String,
    /** Module version used for diagnostics and composition metadata. */
    val version: String,
    /** Module ids that must be started before this module. */
    val dependsOn: Set<String> = emptySet(),
    /** Services that must exist before this module can start. */
    val requires: Set<ServiceKey<*>> = emptySet(),
    /** Services this module promises to register. */
    val provides: Set<ServiceKey<*>> = emptySet(),
) {
    init {
        require(id.isNotBlank()) { "module id must not be blank" }
        require(version.isNotBlank()) { "module version must not be blank" }
        require(id !in dependsOn) { "module $id cannot depend on itself" }
    }
}
