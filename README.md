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

```kotlin
val serviceKey = serviceKey<MyService>("example.my-service")
val registry = DefaultServiceRegistry()

registry.register(serviceKey, MyService())
val service = registry.require(serviceKey)
```

```kotlin
val graph = ModuleGraphValidator.validate(
    listOf(providerA.descriptor, providerB.descriptor)
)
```

## License

Licensed under the Apache License, Version 2.0.
