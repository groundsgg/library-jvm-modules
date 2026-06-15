package gg.grounds.modules

interface ModuleProvider<M : Any> {
    val descriptor: ModuleDescriptor

    fun create(): M
}
