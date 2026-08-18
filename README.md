## 🚀 Deepsky App - What is this project about?

The application is created for people who is interested in astronomy discoveries. Every day the new picture related to astronomy is popping up thanks to NASA - APOD service. In addition, every picture is presented with an explanation and title describing what is show no the user. Also, when some pictures has copyright data to it, application shows it.

"Picture of The Day" is available for 24h, but we can preserve it in the local storage by adding them to "The Favourite" list.

<b>Astronomy Picture of the Day (APOD) </b> is originated, written, coordinated, and edited since 1995 by Robert Nemiroff and Jerry Bonnell. The APOD archive contains the largest collection of annotated astronomical images on the internet. In real life, Robert and Jerry are two professional astronomers who spend most of their time researching the universe. Robert is a professor at Michigan Technological University in Houghton, Michigan, USA, while Jerry is a scientist at NASA's Goddard Space Flight Center in Greenbelt, Maryland USA.

## How the design looks like?

Picture of the day screen:

<img src="https://user-images.githubusercontent.com/45050205/157853432-719c282b-2e7d-47e3-9d29-f6aefa767876.jpg" width="280">&nbsp;
<img src="https://user-images.githubusercontent.com/45050205/157853439-281bf1d0-0aa5-402c-90a7-b31a18669c6e.jpg" width="280">&nbsp; &nbsp;<br>

My Favourite screen and one selected item:

<img src="https://user-images.githubusercontent.com/45050205/157853465-68d3fb8b-5e08-4663-9ead-60f245cd6eea.jpg" width="280">&nbsp;
<img src="https://user-images.githubusercontent.com/45050205/157853473-750ef72e-a5e3-404b-b997-71d472784652.jpg" width="280">&nbsp;
<img src="https://user-images.githubusercontent.com/45050205/157853484-b6bc93a0-a58c-404f-ba81-54436205d971.jpg" width="280">&nbsp;
<br>

## What technologies are used?

Version 2.0 is a **Kotlin Multiplatform** rewrite: the app runs on **Android and iOS** from one shared
codebase, and the whole UI — every screen, not just the business logic — is written once in
**Compose Multiplatform**. The Android-only 1.x stack (Retrofit, Glide, PhotoView, Hilt, LiveData,
XML layouts and the Navigation Component) is gone; it can still be read in git history under the tag
`1.2.1`.

| Concern | Library |
|---|---|
| UI | Compose Multiplatform (Material 3) |
| Navigation | Navigation 3 (`NavDisplay` + typed `NavKey` destinations) |
| Presentation | MVI — `StateActionsViewModel` over `StateFlow` + a one-shot action channel |
| Networking | Ktor Client (OkHttp on Android, Darwin on iOS) + kotlinx.serialization |
| Persistence | Room KMP with SQLite bundled, hand-written migrations |
| Dependency injection | Koin |
| Images | Coil 3 (`AsyncImage`) + `zoomable` for pinch-to-zoom |
| Logging | Timber on Android, `NSLog` on iOS, behind a shared `Logger` interface |
| Concurrency | Kotlin Coroutines + Flow |

**Clean Architecture** still separates the layers, and the module graph enforces it: features depend on
`:domain`, never on each other, and `:core:*` modules are leaves.

```
:domain             # models, interactors, repository interfaces, sealed Result
:data               # Ktor APOD client, Room database, mappers, repository implementations
:core:common        # DateFormatter, Logger, NetworkMonitor
:core:designsystem  # theme, TopSnackbar, shared vector icons
:core:mvvm          # StateActionsViewModel + ActionsEffect (MVI plumbing)
:core:navigation    # typed NavKey destinations
:feature:picture    # Picture of the Day + Picture Details
:feature:favourites # the favourites grid
:feature:about      # the static About screen
:shared             # navigation host, bottom bar, DI aggregation, iOS entry point
:androidApp         # Android entry point
:iosApp             # iOS entry point (SwiftUI shell)
```

Favourites deliberately store **metadata only** — no bitmaps in the database — and Coil re-fetches the
image, which is what let the old `Bitmap`→`ByteArray` TypeConverter go away. Users upgrading from 1.2.1
keep their saved pictures: the database file name is unchanged and `MIGRATION_1_2` copies the rows out
of the old table.

Minimum Android version is **API 26 (Android 8.0)**; iOS targets are `iosArm64` and `iosSimulatorArm64`.

## How to build and run it?

The APOD API key is read from `local.properties`, a Gradle property, or the `APOD_API_KEY` environment
variable — get a free one at <https://api.nasa.gov>:

```
APOD_API_KEY=your_key_here
```

```bash
./gradlew :androidApp:installDebug   # Android
./gradlew allTests                   # the whole suite, Android host + iOS simulator
```

For iOS, open `iosApp/iosApp.xcodeproj` in Xcode and run the `iosApp` scheme.

## Who is the author?

👨‍💻 Implemented by: [Wojciech Kula] <br>
🌌 Powered by: [NASA-APOD]

[NASA-APOD]: <https://apod.nasa.gov/apod/>
[Wojciech Kula]: <https://www.linkedin.com/in/wojciechkula/>
