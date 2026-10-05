# KMP VK Music API

Kotlin Multiplatform library for shared business logic targeting Android, iOS, and JVM.

## Structure

- `library/src/commonMain` contains platform-independent business logic.
- `library/src/androidMain` contains Android-specific implementations.
- `library/src/iosMain` contains iOS-specific implementations.
- `library/src/jvmMain` contains JVM-specific implementations.
- `library/src/commonTest` contains tests shared across supported targets.

## Build

```bash
./gradlew :library:build
```

The project is intentionally library-only. Sample applications can be added later under `samples/` and depend on `:library`.
