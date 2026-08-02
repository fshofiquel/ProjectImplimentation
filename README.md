# ProjectImplimentation

## App idea (useful/novel)
**Pocket Study Sync** helps students manage tasks offline while still interacting with online todo data.  
It combines a local task list (for daily use without internet) with a REST-backed view (for pulling and pushing todo items).

## Two required elements used
1. **Database on device**: Room (SQLite) stores local todo items.
2. **Reading data from a web service**: Retrofit loads todo data from JSONPlaceholder.

## Internal implementation
- **MainActivity**
  - Handles menu switching between Local Todo and REST sections.
  - Wires button actions for add, fetch, upload, and transfer flows.
  - Uses a single background executor for DB and network work.
- **Local persistence (`data/`)**
  - `NoteEntity`: Room table model (`id`, `content`, `completed`).
  - `NoteDao`: `insert`, `update`, `delete`, `getAll`.
  - `AppDatabase`: singleton Room database with migration support.
- **Network layer (`network/`)**
  - `TodoService`: `GET /todos` and `POST /todos`.
  - `TodoResponse`: REST response model.
  - `TodoUploadRequest`: payload model for uploading local todos.
- **UI behavior**
  - Local list supports add, mark complete/incomplete, delete.
  - REST list supports fetch and transfer of REST items back to local.
  - Local items can be uploaded to REST and shown in REST-side list.

## What works
- Local todo CRUD with Room-backed persistence.
- Completion tracking for local items.
- REST fetch and display of todo items.
- Transfer local → REST and REST → local.
- Basic error/status messaging for network operations.

## What could not be completed here
- Emulator-based screenshot capture in this sandbox environment.

## Screenshot checklist (capture in emulator)
Add multiple screenshots that show each feature:
- Home screen with menu buttons visible.
- Adding a local todo item.
- Marking a local todo completed.
- Removing a local todo item.
- Fetching REST todo items.
- Uploading a local todo to REST side.
- Transferring a REST todo back to local list.
