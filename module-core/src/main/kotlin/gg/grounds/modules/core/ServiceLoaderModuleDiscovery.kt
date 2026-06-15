package gg.grounds.modules.core

import gg.grounds.modules.ModuleProvider
import java.util.ServiceLoader

object ServiceLoaderModuleDiscovery {
    fun <P : ModuleProvider<*>> discover(
        providerType: Class<P>,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader,
    ): List<P> {
        return ServiceLoader.load(providerType, classLoader).toList()
    }

    inline fun <reified P : ModuleProvider<*>> discover(
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader
    ): List<P> = discover(P::class.java, classLoader)
}
