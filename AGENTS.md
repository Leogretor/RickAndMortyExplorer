# AGENTS.md

## Project snapshot
- Single-module Android app (`:app`) written in Kotlin with Jetpack Compose, simple MVVM, Retrofit, and coroutines.
- The repository root is this folder: `C:\Users\simon\AndroidStudioProjects\RickAndMortyExplorer`.
- App entry point is `app/src/main/java/com/example/rickandmortyexplorer/MainActivity.kt`.
- Main runtime flow is: `MainActivity` -> `navigation/AppNavHost.kt` -> ViewModels -> `domain/repository/CharacterRepository.kt` -> `data/repository/CharacterRepositoryImpl.kt` -> `data/remote/CharacterApi.kt` via `data/remote/RickAndMortyApi.kt`.

## High-level architecture

### Presentation layer
- `presentation/characters/`
  - `CharactersScreen.kt`: stateless list UI for loading, error, empty, and success states.
  - `CharactersUiState.kt`: sealed UI state.
  - `CharactersViewModel.kt`: loads characters on init and exposes a `StateFlow`.
- `presentation/characterdetail/`
  - `CharacterDetailScreen.kt`: stateless detail UI for loading, error, and success states.
  - `CharacterDetailUiState.kt`: sealed UI state.
  - `CharacterDetailViewModel.kt`: loads one character by `characterId` and exposes a `StateFlow`.

### Domain layer
- `domain/model/Character.kt`
  - Current fields: `id`, `name`, `status`, `species`, `gender`, `origin`, `image`.
- `domain/repository/CharacterRepository.kt`
  - Defines `getCharacters()` and `getCharacterById(id)`.

### Data layer
- `data/remote/CharacterApi.kt`
  - Retrofit service interface with `getAllCharacters()` and `getCharacterById(id)`.
- `data/remote/RickAndMortyApi.kt`
  - Singleton object that creates Retrofit lazily and exposes `service`.
- `data/remote/model/CharactersResponseDto.kt`
  - DTOs for page info, character response, and nested named resources.
- `data/mapper/CharacterMapper.kt`
  - Maps `CharacterDto` to the domain `Character`.
- `data/repository/CharacterRepositoryImpl.kt`
  - Concrete repository implementation used by the app.

### Navigation
- `navigation/NavRoutes.kt`
  - Defines `CHARACTER_LIST`, `CHARACTER_ID_ARG`, `CHARACTER_DETAIL`, and `characterDetail(characterId)`.
- `navigation/AppNavHost.kt`
  - Owns the two destinations and manually creates ViewModels via factories.
  - Detail navigation uses an `Int` route argument (`NavType.IntType`).

## Dependency and build conventions
- Versions and plugin IDs are centralized in `gradle/libs.versions.toml`.
- App module configuration lives in `app/build.gradle.kts`.
- Current stack includes:
  - Compose Material 3
  - Navigation Compose
  - Lifecycle ViewModel + runtime KTX
  - Retrofit + Gson converter
  - Coroutines Android
  - Kotlin serialization plugin and `kotlinx-serialization-json`
- Important nuance: DTOs currently have `@Serializable`, but network calls use Retrofit with `GsonConverterFactory`, so Gson is the active runtime converter.

## Composition and UI conventions
- `MainActivity` manually builds the repository using `CharacterRepositoryImpl(RickAndMortyApi.service)`; there is no DI framework such as Hilt or Dagger.
- Screens are intentionally stateless where practical:
  - pass in a sealed `uiState`
  - pass event callbacks like `onRetry`, `onCharacterClick`, and `onBackClick`
- Each navigation destination wraps its screen in a `Scaffold` and passes `innerPadding` down.
- The list screen explicitly handles an empty list with a visible message.
- Character images are loaded manually in Compose using `URL(...).openStream()` inside `LaunchedEffect` and `Dispatchers.IO`.
- Preview safety matters: both list and detail image composables check `LocalInspectionMode.current` before attempting network image work.
- The project does not currently use Coil or another dedicated image-loading library, even though that would be a common future improvement.

## State management conventions
- ViewModels expose immutable `StateFlow`s backed by private `MutableStateFlow`s.
- `CharactersViewModel` and `CharacterDetailViewModel` both:
  - start in `Loading`
  - trigger loading in `init`
  - cancel previous jobs before retrying
  - catch generic exceptions and surface a user-facing message in UI state
- Both ViewModels accept optional `CoroutineScope` and `CoroutineDispatcher` parameters, which are primarily there to support deterministic tests.

## Testing patterns to follow
- Unit tests live under `app/src/test/java/com/example/rickandmortyexplorer/...`.
- Existing tests cover:
  - DTO shape expectations: `data/remote/model/CharacterDtoSerializationTest.kt`
  - Repository mapping: `data/repository/CharacterRepositoryImplTest.kt`
  - ViewModel state transitions: `presentation/characters/CharactersViewModelTest.kt`
- Coroutine tests currently use `TestCoroutineEnvironment` from `presentation/characters/MainDispatcherRule.kt`.
- Prefer fake repositories or fake APIs over mocks when extending tests.
- Prefer asserting public `uiState` transitions instead of testing private implementation details.

## Workflow notes
- Build with `./gradlew assembleDebug`.
- Run unit tests with `./gradlew test`.
- Run instrumented tests with `./gradlew connectedDebugAndroidTest` when a device/emulator is available.
- The app requires `android.permission.INTERNET` in `app/src/main/AndroidManifest.xml` for API and image requests.
- On this machine, Gradle may require `JAVA_HOME` to be pointed at Android Studio's bundled JBR if Java is not already on `PATH`.

## Change guidance for future work
- If you add new fields from the API, update all of these together:
  - DTOs in `data/remote/model/`
  - `CharacterMapper.kt`
  - `domain/model/Character.kt`
  - previews and tests that construct `Character`
- If you change navigation routes or arguments, update both `NavRoutes.kt` and `AppNavHost.kt` in the same change.
- Keep repository mapping logic in the data layer; do not leak DTOs into presentation code.
- Preserve preview-safe image behavior unless you intentionally replace it with a dedicated image library.
- When changing ViewModels, maintain the existing testability pattern of injectable dispatcher/scope.

## Files to inspect first when making changes
- `app/src/main/java/com/example/rickandmortyexplorer/MainActivity.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/navigation/AppNavHost.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/navigation/NavRoutes.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/data/repository/CharacterRepositoryImpl.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/data/remote/RickAndMortyApi.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/data/remote/CharacterApi.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/data/mapper/CharacterMapper.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/presentation/characters/CharactersViewModel.kt`
- `app/src/main/java/com/example/rickandmortyexplorer/presentation/characterdetail/CharacterDetailViewModel.kt`
- `app/src/test/java/com/example/rickandmortyexplorer/presentation/characters/CharactersViewModelTest.kt`

