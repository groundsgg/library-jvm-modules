# library-jvm-modules

Small typed module and service registry for JVM applications that use build-time classpath composition.

The library provides:

- typed `ServiceKey` and `ServiceRegistry` APIs
- module descriptors with `dependsOn`, `requires`, and `provides`
- deterministic module graph validation and startup ordering
- Java `ServiceLoader` discovery helpers

It is intentionally not a runtime plugin framework. It does not download JARs, create plugin classloaders, or manage application-specific lifecycle hooks.

## Modules

```text
module-api   Public API for module descriptors, providers, service keys, and registries
module-core  Default registry implementation, graph validation, dependency sorting, and discovery
```

## Usage

Use type-first access for normal services:

```kotlin
val registry = DefaultServiceRegistry()

registry.register<MyService>(MyService())

val service = registry.require<MyService>()
```

Use qualified keys only when multiple services share the same contract:

```kotlin
object ExampleServiceKeys {
    val Primary = qualifiedServiceKey<MyService>("example.my-service.primary")
    val Secondary = qualifiedServiceKey<MyService>("example.my-service.secondary")
}

registry.register(ExampleServiceKeys.Primary, primaryService)
registry.register(ExampleServiceKeys.Secondary, secondaryService)
```

Validate module descriptors before startup:

```kotlin
val graph = ModuleGraphValidator.validate(
    listOf(providerA.descriptor, providerB.descriptor)
)
```

See [Service Registry](docs/service-registry.md) for the service key rules.

## License

Licensed under the Apache License, Version 2.0.
