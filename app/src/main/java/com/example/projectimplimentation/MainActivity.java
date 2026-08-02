package com.example.projectimplimentation;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectimplimentation.data.AppDatabase;
import com.example.projectimplimentation.data.NoteEntity;
import com.example.projectimplimentation.network.TodoResponse;
import com.example.projectimplimentation.network.TodoService;
import com.example.projectimplimentation.network.TodoUploadRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText todoInput;
    private TextView todoStatus;
    private TextView webResult;
    private LinearLayout todoSection;
    private LinearLayout restSection;
    private LocalTodoAdapter localTodoAdapter;
    private RestTodoAdapter restTodoAdapter;
    private final List<TodoResponse> restTodos = new ArrayList<>();
    private AppDatabase database;
    private TodoService todoService;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Build long-lived app services once at startup.
        database = AppDatabase.getInstance(getApplicationContext());
        todoService = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoService.class);

        todoInput = findViewById(R.id.todoInput);
        todoStatus = findViewById(R.id.todoStatus);
        webResult = findViewById(R.id.webResult);
        todoSection = findViewById(R.id.todoSection);
        restSection = findViewById(R.id.restSection);

        setupLists();
        setupButtons();
        loadLocalTodos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ioExecutor.shutdown();
    }

    private void setupLists() {
        // Local list callbacks stay focused on local CRUD + upload.
        localTodoAdapter = new LocalTodoAdapter(
                this::updateTodoState,
                this::deleteTodo,
                this::uploadTodoToRest
        );
        // REST list callback performs a move from REST-side list to local DB.
        restTodoAdapter = new RestTodoAdapter(this::downloadTodoToLocal);

        RecyclerView todoRecyclerView = findViewById(R.id.todoRecyclerView);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoRecyclerView.setAdapter(localTodoAdapter);

        RecyclerView restRecyclerView = findViewById(R.id.restRecyclerView);
        restRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restRecyclerView.setAdapter(restTodoAdapter);
    }

    private void setupButtons() {
        findViewById(R.id.todoMenuButton).setOnClickListener(v -> showTodoMenu());
        findViewById(R.id.restMenuButton).setOnClickListener(v -> showRestMenu());
        findViewById(R.id.addTodoButton).setOnClickListener(v -> addTodo());
        findViewById(R.id.fetchTodoButton).setOnClickListener(v -> fetchTodos());
    }

    private void showTodoMenu() {
        todoSection.setVisibility(View.VISIBLE);
        restSection.setVisibility(View.GONE);
    }

    private void showRestMenu() {
        todoSection.setVisibility(View.GONE);
        restSection.setVisibility(View.VISIBLE);
    }

    private void addTodo() {
        String todoText = todoInput.getText().toString().trim();
        if (todoText.isEmpty()) {
            todoStatus.setText(getString(R.string.empty_todo_message));
            return;
        }

        ioExecutor.execute(() -> {
            database.noteDao().insert(new NoteEntity(todoText));
            runOnUiThread(() -> {
                todoInput.getText().clear();
                loadLocalTodos();
            });
        });
    }

    private void loadLocalTodos() {
        ioExecutor.execute(() -> {
            List<NoteEntity> todos = database.noteDao().getAll();
            runOnUiThread(() -> {
                localTodoAdapter.submitList(todos);
                if (todos.isEmpty()) {
                    todoStatus.setText(getString(R.string.no_todos_saved));
                } else {
                    int pending = 0;
                    for (NoteEntity todo : todos) {
                        if (!todo.isCompleted()) {
                            pending++;
                        }
                    }
                    todoStatus.setText(getString(R.string.todo_count_format, pending, todos.size()));
                }
            });
        });
    }

    private void updateTodoState(NoteEntity todo, boolean completed) {
        ioExecutor.execute(() -> {
            // Keep update simple: mutate selected item then persist.
            todo.setCompleted(completed);
            database.noteDao().update(todo);
            runOnUiThread(this::loadLocalTodos);
        });
    }

    private void deleteTodo(NoteEntity todo) {
        ioExecutor.execute(() -> {
            database.noteDao().delete(todo);
            runOnUiThread(this::loadLocalTodos);
        });
    }

    private void downloadTodoToLocal(TodoResponse todo) {
        ioExecutor.execute(() -> {
            // Save to local first...
            database.noteDao().insert(new NoteEntity(todo.getTitle(), todo.isCompleted()));
            // ...then remove from REST-side list so the item cannot be moved repeatedly.
            restTodos.removeIf(item -> item.getId() == todo.getId());
            runOnUiThread(() -> {
                todoStatus.setText(getString(R.string.download_success));
                restTodoAdapter.submitList(new ArrayList<>(restTodos));
                showTodoMenu();
                loadLocalTodos();
            });
        });
    }

    private void uploadTodoToRest(NoteEntity todo) {
        webResult.setText(getString(R.string.web_loading));
        ioExecutor.execute(() -> {
            String statusText;
            try {
                Response<TodoResponse> response = todoService
                        .uploadTodo(new TodoUploadRequest(todo.getContent(), todo.isCompleted()))
                        .execute();
                if (response.isSuccessful() && response.body() != null) {
                    restTodos.add(0, response.body());
                    statusText = getString(R.string.upload_success_format, todo.getContent());
                } else {
                    statusText = getString(
                            R.string.web_error_format,
                            getString(R.string.unknown_error)
                    );
                }
            } catch (IOException exception) {
                statusText = getString(
                        R.string.web_error_format,
                        exception.getMessage() != null ? exception.getMessage() : getString(R.string.unknown_error)
                );
            }

            String finalStatusText = statusText;
            runOnUiThread(() -> {
                webResult.setText(finalStatusText);
                // Always push a snapshot copy to avoid adapter-side mutation surprises.
                restTodoAdapter.submitList(new ArrayList<>(restTodos));
                showRestMenu();
            });
        });
    }

    private void fetchTodos() {
        webResult.setText(getString(R.string.web_loading));
        ioExecutor.execute(() -> {
            String statusText;
            try {
                Response<List<TodoResponse>> response = todoService.getTodos(10).execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<TodoResponse> todos = response.body();
                    restTodos.clear();
                    restTodos.addAll(todos);
                    statusText = getString(R.string.web_success_format, todos.size());
                } else {
                    statusText = getString(
                            R.string.web_error_format,
                            getString(R.string.unknown_error)
                    );
                }
            } catch (IOException exception) {
                statusText = getString(
                        R.string.web_error_format,
                        exception.getMessage() != null ? exception.getMessage() : getString(R.string.unknown_error)
                );
            }

            String finalStatusText = statusText;
            runOnUiThread(() -> {
                webResult.setText(finalStatusText);
                restTodoAdapter.submitList(new ArrayList<>(restTodos));
            });
        });
    }
}
