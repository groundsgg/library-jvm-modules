package gg.grounds.modules

/**
 * Factory discovered from the application classpath.
 *
 * Implementations are commonly loaded through Java [java.util.ServiceLoader]. The created module
 * type is application-specific; this library only models discovery and dependency metadata.
 */
interface ModuleProvider<M : Any> {
    /** Descriptor used for validation and startup ordering before [create] is called. */
    val descriptor: ModuleDescriptor

    /** Creates a new module instance. */
    fun create(): M
}
