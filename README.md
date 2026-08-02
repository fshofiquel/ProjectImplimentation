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
