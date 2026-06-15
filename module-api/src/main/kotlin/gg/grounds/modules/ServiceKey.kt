package gg.grounds.modules

import kotlin.reflect.KClass

/**
 * Typed service identifier used by [ServiceRegistry].
 *
 * Prefer unqualified type keys created with [serviceKey] for normal services. Use
 * [qualifiedServiceKey] only when multiple services implement the same public contract and callers
 * must choose one explicitly.
 */
class ServiceKey<T : Any>(val type: KClass<T>, val qualifier: String? = null) {
    /**
     * Stable identifier used in diagnostics and graph validation.
     *
     * For unqualified keys this is the service type's qualified class name. For qualified keys this
     * is the explicit qualifier supplied by the API owner.
     */
    val id: String =
        qualifier
            ?: requireNotNull(type.qualifiedName) {
                "service key type ${type.simpleName ?: type} must have a qualified name"
            }

    init {
        require(qualifier == null || qualifier.isNotBlank()) {
            "service key qualifier must not be blank"
        }
    }

    final override fun equals(other: Any?): Boolean {
        return other is ServiceKey<*> && type == other.type && qualifier == other.qualifier
    }

    final override fun hashCode(): Int = 31 * type.hashCode() + qualifier.hashCode()

    final override fun toString(): String {
        val typeName = type.qualifiedName ?: type.toString()
        return if (qualifier == null) {
            typeName
        } else {
            "$qualifier:$typeName"
        }
    }
}

/**
 * Creates the default key for a service type.
 *
 * This is the normal path for services where there is one registered implementation for [T].
 */
inline fun <reified T : Any> serviceKey(): ServiceKey<T> = serviceKey(T::class)

/**
 * Creates the default key for [type].
 *
 * Use this overload from non-reified code.
 */
fun <T : Any> serviceKey(type: KClass<T>): ServiceKey<T> = ServiceKey(type)

/**
 * Creates an explicit qualified key for a service type.
 *
 * Use qualified keys only when the same service contract has multiple registered implementations.
 */
inline fun <reified T : Any> qualifiedServiceKey(qualifier: String): ServiceKey<T> =
    qualifiedServiceKey(T::class, qualifier)

/**
 * Creates an explicit qualified key for [type].
 *
 * Use this overload from non-reified code.
 */
fun <T : Any> qualifiedServiceKey(type: KClass<T>, qualifier: String): ServiceKey<T> =
    ServiceKey(type = type, qualifier = qualifier)
