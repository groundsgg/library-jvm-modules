package gg.grounds.modules

import kotlin.reflect.KClass

open class ServiceKey<T : Any>(val id: String, val type: KClass<T>) {
    init {
        require(id.isNotBlank()) { "service key id must not be blank" }
    }

    final override fun equals(other: Any?): Boolean {
        return other is ServiceKey<*> && id == other.id && type == other.type
    }

    final override fun hashCode(): Int = 31 * id.hashCode() + type.hashCode()

    final override fun toString(): String = "$id:${type.qualifiedName}"
}

inline fun <reified T : Any> serviceKey(id: String): ServiceKey<T> = ServiceKey(id, T::class)
