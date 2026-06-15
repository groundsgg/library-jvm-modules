# Service Registry

`library-jvm-modules` uses typed service keys so modules can publish and consume services without string lookups at call sites.

## Default: Access Services By Type

Use type-first access when there is one implementation for a service contract:

```kotlin
val registry = DefaultServiceRegistry()

registry.register<MyService>(MyServiceImpl())

val service = registry.require<MyService>()
```

For non-reified call sites, pass the Kotlin class:

```kotlin
registry.register(MyService::class, MyServiceImpl())

val service = registry.require(MyService::class)
```

## Multiple Implementations: Use Qualified Keys

Use qualified keys only when the same contract has multiple implementations:

```kotlin
object NatsServiceKeys {
    val Public = qualifiedServiceKey<NatsClient>("grounds.nats.public")
    val Internal = qualifiedServiceKey<NatsClient>("grounds.nats.internal")
}
```

Register and read those keys explicitly:

```kotlin
registry.register(NatsServiceKeys.Public, publicNats)
registry.register(NatsServiceKeys.Internal, internalNats)

val publicNats = registry.require(NatsServiceKeys.Public)
```

Do not create qualified keys inline at call sites. Define them once in the module API that owns the service contract.

## Module Descriptors

Use the same keys in module descriptors:

```kotlin
class MatchmakingModuleProvider : ModuleProvider<MatchmakingModule> {
    override val descriptor =
        ModuleDescriptor(
            id = "grounds.matchmaking",
            version = "1.0.0",
            requires = setOf(NatsServiceKeys.Internal),
            provides = setOf(serviceKey<MatchmakingService>()),
        )

    override fun create(): MatchmakingModule = MatchmakingModule()
}
```

`dependsOn` is for module startup order. `requires` and `provides` are for service contracts.
