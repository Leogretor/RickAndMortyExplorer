# AGENTS.md

## Repository summary

`RickAndMortyExplorer` is a single-module Android app built with:

- Kotlin
- Jetpack Compose
- MVVM
- Retrofit + Gson
- OkHttp
- Kotlin coroutines
- Coil for image loading

Repository root:
- `C:\Users\simon\AndroidStudioProjects\RickAndMortyExplorer`

App entry point:
- `app/src/main/java/com/example/rickandmortyexplorer/MainActivity.kt`

Main runtime flow:
- `MainActivity` -> `navigation/AppNavHost.kt` -> ViewModels -> `domain/repository/CharacterRepository.kt` -> `data/repository/CharacterRepositoryImpl.kt` -> `data/remote/CharacterApi.kt` via `data/remote/RickAndMortyApi.kt`

## High-level architecture

The project uses a lightweight layered structure:

### Presentation layer
- `presentation/characters/`
  - `CharactersScreen.kt`: stateless list UI for loading, error, empty, and success states
  - `CharactersUiState.kt`: sealed screen state
  - `CharactersViewModel.kt`: loads the character list and exposes a `StateFlow`
- `presentation/characterdetail/`
  - `CharacterDetailScreen.kt`: stateless detail UI for loading, error, and success states
  - `CharacterDetailUiState.kt`: sealed screen state
  - `CharacterDetailViewModel.kt`: loads a single character by id and exposes a `StateFlow`
- `presentation/common/`
  - `CharacterAsyncImage.kt`: shared Coil-based image composable with preview-safe fallback behavior
  - `ThrowableMessage.kt`: shared mapping from exceptions to user-facing error messages

### Domain layer
- `domain/model/Character.kt`
  - Current fields: `id`, `name`, `status`, `species`, `gender`, `origin`, `image`
- `domain/repository/CharacterRepository.kt`
  - Defines `getCharacters()` and `getCharacterById(id)`

### Data layer
- `data/remote/CharacterApi.kt`
  - Retrofit API interface with `getAllCharacters()` and `getCharacterById(id)`
- `data/remote/RickAndMortyApi.kt`
  - Lazily creates Retrofit with Gson and an `OkHttpClient` with timeouts
- `data/remote/model/CharactersResponseDto.kt`
  - DTOs for page info, character response, and nested named resources
- `data/mapper/CharacterMapper.kt`
  - Maps `CharacterDto` to the domain `Character`
- `data/repository/CharacterRepositoryImpl.kt`
  - Concrete repository implementation used by the app

### Navigation
- `navigation/NavRoutes.kt`
  - Defines `CHARACTER_LIST`, `CHARACTER_ID_ARG`, `CHARACTER_DETAIL`, and `characterDetail(characterId)`
- `navigation/AppNavHost.kt`
  - Owns the two destinations and manually creates ViewModels via factories
  - Uses `collectAsStateWithLifecycle()` for UI state collection
  - Handles missing detail arguments with an explicit error state instead of defaulting to `0`

## Current implementation conventions

### Compose and UI
- Keep screens stateless where practical: pass in `uiState` and callbacks such as `onRetry`, `onCharacterClick`, and `onBackClick`
- Image loading should go through `presentation/common/CharacterAsyncImage.kt`; do not reintroduce manual `URL(...).openStream()` image decoding in composables
- Preview safety matters: shared image rendering checks `LocalInspectionMode` and falls back to placeholder content in previews
- Navigation destinations may wrap screens in `Scaffold` and pass `innerPadding` down to the content composable

### State management
- ViewModels expose immutable `StateFlow`s backed by private `MutableStateFlow`s
- `CharactersViewModel` and `CharacterDetailViewModel` both:
  - start in `Loading`
  - trigger loading in `init`
  - cancel previous jobs before retrying
  - set loading state immediately on retry
  - map exceptions to user-facing messages with `toUserMessage(...)`
- Both ViewModels accept optional `CoroutineScope` and `CoroutineDispatcher` parameters to support deterministic tests

### Error handling
- Keep raw exception details out of the UI
- Extend `presentation/common/ThrowableMessage.kt` when new failure cases need user-friendly messages
- Prefer mapping network and transport failures centrally rather than formatting ad hoc strings in each screen

### Dependencies and build
- Versions and plugin IDs are centralized in `gradle/libs.versions.toml`
- App module configuration lives in `app/build.gradle.kts`
- Active runtime stack includes:
  - Compose Material 3
  - Navigation Compose
  - Lifecycle runtime compose + ViewModel
  - Retrofit + Gson
  - OkHttp
  - Coil Compose
  - Coroutines Android
- Kotlin serialization is not part of the current runtime path

## Testing conventions
- Unit tests live under `app/src/test/java/com/example/rickandmortyexplorer/...`
- Existing coverage includes:
  - DTO shape expectations: `data/remote/model/CharacterDtoSerializationTest.kt`
  - Repository mapping: `data/repository/CharacterRepositoryImplTest.kt`
  - ViewModel state transitions: `presentation/characters/CharactersViewModelTest.kt`
  - Error message mapping: `presentation/common/ThrowableMessageTest.kt`
- Coroutine tests use `TestCoroutineEnvironment` from `presentation/characters/MainDispatcherRule.kt`
- Prefer fake repositories or fake APIs over mocks where practical
- Prefer asserting public `uiState` transitions instead of private implementation details

## Workflow notes
- Debug build:
  - `./gradlew assembleDebug`
- Unit tests:
  - `./gradlew test`
- Instrumented tests:
  - `./gradlew connectedDebugAndroidTest`
- The app requires `android.permission.INTERNET` in `app/src/main/AndroidManifest.xml`
- On this machine, Gradle may require `JAVA_HOME` to point to Android Studio's bundled JBR:
  - `C:\Program Files\Android\Android Studio\jbr`

## Change guidance
- If you add new API fields, update all of these together:
  - DTOs in `data/remote/model/`
  - `data/mapper/CharacterMapper.kt`
  - `domain/model/Character.kt`
  - previews and tests that construct `Character`
- If you change navigation routes or arguments, update both `navigation/NavRoutes.kt` and `navigation/AppNavHost.kt`
- Keep repository mapping logic in the data layer; do not leak DTOs into presentation code
- Preserve the existing ViewModel testability pattern of injectable dispatcher/scope
- Prefer small, incremental changes unless there is a clear need for a larger refactor such as DI, paging, or persistence
