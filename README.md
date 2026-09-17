# RickAndMortyExplorer

A small Android app that loads characters from the
[Rick and Morty API](https://rickandmortyapi.com/), presents them in a list, and
opens a detail screen when a character is selected.

## Features

- Character list and detail navigation
- Loading, empty, and retryable error states
- Lifecycle-aware UI state collection
- Character images loaded with Coil
- User-friendly handling of common network failures

## Technical approach

The app is a single-module Kotlin project using Jetpack Compose, MVVM, Retrofit,
Gson, OkHttp, coroutines, and Navigation Compose.

The code is separated into lightweight layers:

- `presentation`: stateless Compose screens, UI state, and ViewModels
- `domain`: the app-facing `Character` model and repository contract
- `data`: Retrofit DTOs, mapping, and the repository implementation
- `navigation`: routes and destination wiring

Runtime flow:

`MainActivity` -> `AppNavHost` -> ViewModels -> `CharacterRepository` ->
`CharacterRepositoryImpl` -> `CharacterApi`

## Design decisions

- Screens receive state and callbacks instead of owning ViewModels, which keeps
  UI rendering easy to preview and test.
- ViewModels expose immutable `StateFlow` values and use explicit loading,
  success, and error states.
- Network DTOs are mapped to a small domain model before reaching the UI.
- Stable UI copy is backed by string resources through `UiText`.
- Image loading and common loading/error UI are shared to avoid duplicated
  behavior.
- Dependencies are wired manually in `MainActivity`. A DI framework would add
  more setup than value at this project size.

## Scope and tradeoffs

The app intentionally loads only the first API page. Pagination, offline
persistence, search, and dependency injection would be reasonable next steps
for a production app, but were left out to keep this exercise focused on the
requested list/detail flow.

The detail screen fetches a character by ID instead of depending on list state.
This adds one request but keeps the destination independently loadable.

## Testing

Unit tests cover:

- DTO deserialization expectations
- DTO-to-domain repository mapping
- List and detail ViewModel state transitions and retries
- User-facing throwable mapping

Tests use `kotlinx-coroutines-test` for deterministic coroutine execution.

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat test --console=plain
```

Build a debug APK with:

```powershell
.\gradlew.bat assembleDebug --console=plain
```

The app requires `android.permission.INTERNET` for API and image requests.
