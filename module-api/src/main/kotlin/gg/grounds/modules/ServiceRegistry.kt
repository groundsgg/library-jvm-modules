package gg.grounds.modules

interface ServiceRegistry {
    fun <T : Any> register(key: ServiceKey<T>, service: T)

    fun <T : Any> get(key: ServiceKey<T>): T?

    fun <T : Any> require(key: ServiceKey<T>): T {
        return get(key) ?: throw MissingServiceException(key)
    }

    fun contains(key: ServiceKey<*>): Boolean = getUntyped(key) != null

    fun keys(): Set<ServiceKey<*>>

    fun getUntyped(key: ServiceKey<*>): Any?
}

class MissingServiceException(key: ServiceKey<*>) :
    IllegalStateException("required service is not registered: $key")

class DuplicateServiceException(key: ServiceKey<*>) :
    IllegalStateException("service is already registered: $key")
