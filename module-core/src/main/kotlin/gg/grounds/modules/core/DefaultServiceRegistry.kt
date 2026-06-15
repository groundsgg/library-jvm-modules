package gg.grounds.modules.core

import gg.grounds.modules.DuplicateServiceException
import gg.grounds.modules.ServiceKey
import gg.grounds.modules.ServiceRegistry
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe [ServiceRegistry] backed by a concurrent map.
 *
 * Services are immutable bindings: a key can be registered once and duplicate registration fails.
 */
class DefaultServiceRegistry : ServiceRegistry {
    private val services = ConcurrentHashMap<ServiceKey<*>, Any>()

    override fun <T : Any> register(key: ServiceKey<T>, service: T) {
        require(key.type.isInstance(service)) {
            "service ${service::class.qualifiedName} is not an instance of ${key.type.qualifiedName}"
        }
        val previous = services.putIfAbsent(key, service)
        if (previous != null) {
            throw DuplicateServiceException(key)
        }
    }

    override fun <T : Any> get(key: ServiceKey<T>): T? {
        val service = services[key] ?: return null
        if (!key.type.isInstance(service)) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        return service as T
    }

    override fun keys(): Set<ServiceKey<*>> = services.keys.toSet()

    override fun getUntyped(key: ServiceKey<*>): Any? = services[key]
}
