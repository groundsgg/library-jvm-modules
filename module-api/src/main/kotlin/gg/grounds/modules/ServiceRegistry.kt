package gg.grounds.modules

import kotlin.reflect.KClass

/**
 * Registry for typed module services.
 *
 * Implementations store services by [ServiceKey]. Consumers should normally use the type-first
 * extension functions in this file, such as `registry.require<MyService>()`.
 */
interface ServiceRegistry {
    /**
     * Registers [service] under [key].
     *
     * Throws [DuplicateServiceException] if the key is already registered.
     */
    fun <T : Any> register(key: ServiceKey<T>, service: T)

    /** Returns the service registered under [key], or `null` when no service exists. */
    fun <T : Any> get(key: ServiceKey<T>): T?

    /**
     * Returns the service registered under [key].
     *
     * Throws [MissingServiceException] when no service exists.
     */
    fun <T : Any> require(key: ServiceKey<T>): T {
        return get(key) ?: throw MissingServiceException(key)
    }

    /** Returns true when [key] has a registered service. */
    fun contains(key: ServiceKey<*>): Boolean = getUntyped(key) != null

    /** Returns a snapshot of currently registered service keys. */
    fun keys(): Set<ServiceKey<*>>

    /**
     * Returns the raw service registered under [key].
     *
     * Prefer typed accessors for application code. This method exists for diagnostics and registry
     * internals that need to inspect unknown service types.
     */
    fun getUntyped(key: ServiceKey<*>): Any?
}

/** Registers [service] under the default key for [T]. */
inline fun <reified T : Any> ServiceRegistry.register(service: T) {
    register(serviceKey<T>(), service)
}

/** Registers [service] under the default key for [type]. */
fun <T : Any> ServiceRegistry.register(type: KClass<T>, service: T) {
    register(serviceKey(type), service)
}

/** Returns the service registered for [T], or `null`. */
inline fun <reified T : Any> ServiceRegistry.get(): T? = get(serviceKey<T>())

/** Returns the service registered for [type], or `null`. */
fun <T : Any> ServiceRegistry.get(type: KClass<T>): T? = get(serviceKey(type))

/**
 * Returns the service registered for [T].
 *
 * Throws [MissingServiceException] when no service exists.
 */
inline fun <reified T : Any> ServiceRegistry.require(): T = require(serviceKey<T>())

/**
 * Returns the service registered for [type].
 *
 * Throws [MissingServiceException] when no service exists.
 */
fun <T : Any> ServiceRegistry.require(type: KClass<T>): T = require(serviceKey(type))

/** Returns true when [T] has a registered service. */
inline fun <reified T : Any> ServiceRegistry.contains(): Boolean = contains(serviceKey<T>())

/** Returns true when [type] has a registered service. */
fun <T : Any> ServiceRegistry.contains(type: KClass<T>): Boolean = contains(serviceKey(type))

/** Thrown when a required service is not registered. */
class MissingServiceException(key: ServiceKey<*>) :
    IllegalStateException("required service is not registered: $key")

/** Thrown when a service key is registered more than once. */
class DuplicateServiceException(key: ServiceKey<*>) :
    IllegalStateException("service is already registered: $key")
