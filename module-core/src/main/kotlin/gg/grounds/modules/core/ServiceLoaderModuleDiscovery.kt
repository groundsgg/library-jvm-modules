package gg.grounds.modules.core

import gg.grounds.modules.ModuleProvider
import java.util.ServiceLoader

/** Discovers module providers from Java [ServiceLoader] metadata on the application classpath. */
object ServiceLoaderModuleDiscovery {
    /** Loads providers of [providerType] using [classLoader]. */
    fun <P : ModuleProvider<*>> discover(
        providerType: Class<P>,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader,
    ): List<P> {
        return ServiceLoader.load(providerType, classLoader).toList()
    }

    /** Loads providers of [P] using [classLoader]. */
    inline fun <reified P : ModuleProvider<*>> discover(
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader
    ): List<P> = discover(P::class.java, classLoader)
}
