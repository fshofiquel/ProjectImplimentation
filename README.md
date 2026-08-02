# ProjectImplimentation

## Pocket Study Sync
Pocket Study Sync is an Android app that combines a local todo list with a REST-backed todo view. It is designed for students who want to manage tasks offline while still being able to fetch and exchange items with online todo data.

## Core features
- Create, view, update, and delete local todo items.
- Mark local todos as completed or pending.
- Fetch todo items from a remote REST API.
- Upload local todo items to the REST-side list.
- Transfer fetched REST items back into local storage.

## Internal implementation
- **Language and platform**
  - Java-based Android app.
  - Minimum SDK 29, target SDK 34.
- **Architecture**
  - `MainActivity` manages UI actions, menu switching, and background execution.
  - A single `ExecutorService` handles database and network operations off the main thread.
- **Local data layer (`data/`)**
  - `NoteEntity` defines the Room table model (`id`, `content`, `completed`).
  - `NoteDao` provides `insert`, `update`, `delete`, and `getAll` operations.
  - `AppDatabase` provides a singleton Room database instance with migration support.
- **Network layer (`network/`)**
  - `TodoService` defines REST endpoints for loading and uploading todos.
  - `TodoResponse` maps incoming API response data.
  - `TodoUploadRequest` defines the upload payload.
- **UI layer**
  - Two sections in the main screen: Local Todo and REST Side.
  - RecyclerView adapters (`LocalTodoAdapter`, `RestTodoAdapter`) render and manage item interactions.
  - Status text provides user feedback for loading, success, and error states.

## Current status
- Local Room persistence and todo CRUD behavior are implemented.
- REST fetch and upload flows are implemented.
- Local/REST transfer behavior is implemented in both directions.
- The app can be used today for managing and syncing study todos between local storage and a REST source.

## What works
- Creating, updating, deleting, and toggling completion on local todo items.
- Viewing local todos in a RecyclerView backed by Room.
- Fetching todo items from the REST endpoint.
- Uploading local todos to the REST-side list.
- Copying REST items into local storage.

## What could not be completed yet
- User accounts/authentication for personal cloud sync.
- Conflict handling for simultaneous edits across devices.
- Rich filtering, sorting, and deadline/reminder support.
- These were not completed due to project scope/time limits.

## App idea (useful/novel value)
Pocket Study Sync is designed as a useful student productivity app that bridges offline-first task management with simple online todo exchange.  
Its value is that students can continue working without internet, then fetch/upload tasks when connectivity returns.

## Two required elements used
1. **Single-user app**: The current app is built for one user on one device.
2. **Real usefulness**: It solves a practical study-planning problem by combining offline storage and online task sync.
