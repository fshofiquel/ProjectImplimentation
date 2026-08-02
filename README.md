# ProjectImplimentation

## App idea (useful/novel)
**Pocket Study Sync** is a lightweight Android app for students to quickly save short study notes locally and fetch a sample task from an online REST service to compare offline and online learning tasks in one place.

## Two required mobile elements used
1. **Database to store app data on the mobile device** (Room SQLite database)
2. **Reading data from a web service** (Retrofit call to JSONPlaceholder REST API)

## Internal implementation description
- **Platform/SDK**
  - Android Studio Hedgehog compatibility
  - Minimum SDK: **API 29 (Android 10)**
  - Build config: **Groovy DSL**
- **Architecture (small, focused)**
  - `MainActivity` coordinates UI actions and asynchronous work with `lifecycleScope` coroutines.
  - `data/` package contains Room persistence:
    - `NoteEntity` (table model)
    - `NoteDao` (`insert`, `getAll`)
    - `AppDatabase` (singleton Room DB)
  - `network/` package contains REST integration:
    - `TodoService` (Retrofit interface)
    - `TodoResponse` (response model)
- **UI flow**
  - User types a note and saves it to the local Room database.
  - User can load and display all saved notes.
  - User can fetch data from a REST endpoint (`https://jsonplaceholder.typicode.com/todos/1`) and view the result.

## What works / what could not be fully verified
### Works (implemented in code)
- Local note persistence via Room on-device database.
- Retrieval and display of stored notes.
- REST fetch from a web service and display of fetched values.
- Error handling text for network failures.

### Could not be fully verified in this environment
- Full emulator runtime verification and screenshot capture are not available in this sandboxed environment.
- The screenshot section below includes required file names/locations to add emulator captures during final run.

## Screenshots (multiple, showing each feature)
Place emulator screenshots in `/home/runner/work/ProjectImplimentation/ProjectImplimentation/docs/screenshots/` with these names:
- `01-home-screen.png` (initial UI)
- `02-save-note.png` (saving to DB)
- `03-load-notes.png` (loaded DB records)
- `04-fetch-web-data.png` (REST data shown)

## Code root for submission
Use the full repository root (`/home/runner/work/ProjectImplimentation/ProjectImplimentation`) as the app code folder when creating the final zip submission.
