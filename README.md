# RickAndMortyExplorer

A small Android app built with **Kotlin**, **Jetpack Compose**, **MVVM**, **Retrofit**, and **coroutines** that explores characters from the Rick and Morty API.

## What the app does

- Shows a list of Rick and Morty characters
- Opens a detail screen for an individual character
- Handles loading, empty, and retry states
- Loads character artwork with Coil
- Uses Material-style top app bar navigation on the detail screen

## Architecture overview

The project is a single-module Android app with a lightweight layered structure:

- `presentation/`
  - Compose screens
  - screen UI state models
  - ViewModels
  - shared UI helpers
- `domain/`
  - `Character` domain model
  - `CharacterRepository` contract
- `data/`
  - Retrofit API interface and client
  - DTOs and mappers
  - repository implementation
- `navigation/`
  - app routes and Compose navigation host

Main runtime flow:

`MainActivity` -> `navigation/AppNavHost.kt` -> ViewModels -> `domain/repository/CharacterRepository.kt` -> `data/repository/CharacterRepositoryImpl.kt` -> `data/remote/CharacterApi.kt`

## Recent improvements

### Cleanup pass

- replaced manual `URL(...).openStream()` image loading with **Coil**
- introduced a shared `CharacterAsyncImage` composable in `presentation/common/`
- switched navigation destinations to lifecycle-aware state collection with `collectAsStateWithLifecycle()`
- improved user-facing error handling with centralized throwable-to-message mapping
- removed the unsafe `characterId ?: 0` fallback in detail navigation
- added basic `OkHttpClient` timeouts to the Retrofit setup
- removed unused Kotlin serialization setup so the build matches the actual Gson-based runtime behavior
- removed the unused `RickAndMortyApiService.kt`

### UI polish pass

- introduced reusable `LoadingStateContent` and `ErrorStateContent` composables
- added a small `UiText` abstraction so screens and ViewModels can use Android string resources cleanly
- moved user-facing screen text and error messages into `strings.xml`
- replaced the detail screen’s in-content back button with a proper Material `TopAppBar`
- simplified `AppNavHost.kt` so the detail screen owns its own scaffold behavior

## Key files

- `app/src/main/java/com/example/rickandmortyexplorer/MainActivity.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/navigation/AppNavHost.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/presentation/characters/CharactersScreen.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/presentation/characterdetail/CharacterDetailScreen.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/presentation/common/CharacterAsyncImage.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/presentation/common/ScreenStateContent.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/presentation/common/ThrowableMessage.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/presentation/common/UiText.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/java/com/example/rickandmortyexplorer/data/remote/RickAndMortyApi.kt`

## Build and test

This workspace uses the Gradle wrapper.

### Unit tests

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat test --console=plain
```

### Debug build

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat assembleDebug --console=plain
```

## Dependencies in active use

- Jetpack Compose Material 3
- Compose Material Icons Extended
- Navigation Compose
- Lifecycle ViewModel + runtime compose
- Retrofit + Gson
- OkHttp
- Coil Compose
- Kotlin coroutines

## Notes

- `android.permission.INTERNET` is required for API and image requests.
- The app currently fetches the first page of characters from the API.
- There is no DI framework yet; dependencies are still wired manually for now.
