package com.example.projectimplimentation

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectimplimentation.data.AppDatabase
import com.example.projectimplimentation.data.NoteEntity
import com.example.projectimplimentation.network.TodoResponse
import com.example.projectimplimentation.network.TodoService
import com.example.projectimplimentation.network.TodoUploadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var todoInput: EditText
    private lateinit var todoStatus: TextView
    private lateinit var webResult: TextView
    private lateinit var todoSection: LinearLayout
    private lateinit var restSection: LinearLayout
    private lateinit var localTodoAdapter: LocalTodoAdapter
    private lateinit var restTodoAdapter: RestTodoAdapter
    private val restTodos = mutableListOf<TodoResponse>()

    private val database by lazy { AppDatabase.getInstance(applicationContext) }
    private val todoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TodoService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoInput = findViewById(R.id.todoInput)
        todoStatus = findViewById(R.id.todoStatus)
        webResult = findViewById(R.id.webResult)
        todoSection = findViewById(R.id.todoSection)
        restSection = findViewById(R.id.restSection)

        setupLists()
        setupButtons()
        loadLocalTodos()
    }

    private fun setupLists() {
        localTodoAdapter = LocalTodoAdapter(
            onToggleComplete = { todo, completed -> updateTodoState(todo, completed) },
            onDelete = { todo -> deleteTodo(todo) },
            onTransferToRest = { todo -> uploadTodoToRest(todo) }
        )
        restTodoAdapter = RestTodoAdapter(
            onTransferToTodo = { todo -> downloadTodoToLocal(todo) }
        )

        findViewById<RecyclerView>(R.id.todoRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = localTodoAdapter
        }

        findViewById<RecyclerView>(R.id.restRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = restTodoAdapter
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.todoMenuButton).setOnClickListener { showTodoMenu() }
        findViewById<Button>(R.id.restMenuButton).setOnClickListener { showRestMenu() }
        findViewById<Button>(R.id.addTodoButton).setOnClickListener { addTodo() }
        findViewById<Button>(R.id.fetchTodoButton).setOnClickListener { fetchTodos() }
    }

    private fun showTodoMenu() {
        todoSection.visibility = View.VISIBLE
        restSection.visibility = View.GONE
    }

    private fun showRestMenu() {
        todoSection.visibility = View.GONE
        restSection.visibility = View.VISIBLE
    }

    private fun addTodo() {
        val todoText = todoInput.text.toString().trim()
        if (todoText.isEmpty()) {
            todoStatus.text = getString(R.string.empty_todo_message)
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            database.noteDao().insert(NoteEntity(content = todoText))
            withContext(Dispatchers.Main) {
                todoInput.text?.clear()
                loadLocalTodos()
            }
        }
    }

    private fun loadLocalTodos() {
        lifecycleScope.launch(Dispatchers.IO) {
            val todos = database.noteDao().getAll()
            withContext(Dispatchers.Main) {
                localTodoAdapter.submitList(todos)
                todoStatus.text = if (todos.isEmpty()) {
                    getString(R.string.no_todos_saved)
                } else {
                    getString(
                        R.string.todo_count_format,
                        todos.count { !it.completed },
                        todos.size
                    )
                }
            }
        }
    }

    private fun updateTodoState(todo: NoteEntity, completed: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            database.noteDao().update(todo.copy(completed = completed))
            withContext(Dispatchers.Main) {
                loadLocalTodos()
            }
        }
    }

    private fun deleteTodo(todo: NoteEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            database.noteDao().delete(todo)
            withContext(Dispatchers.Main) {
                loadLocalTodos()
            }
        }
    }

    private fun downloadTodoToLocal(todo: TodoResponse) {
        lifecycleScope.launch(Dispatchers.IO) {
            database.noteDao().insert(
                NoteEntity(content = todo.title, completed = todo.completed)
            )
            restTodos.removeAll { it.id == todo.id }
            withContext(Dispatchers.Main) {
                todoStatus.text = getString(R.string.download_success)
                restTodoAdapter.submitList(restTodos.toList())
                showTodoMenu()
                loadLocalTodos()
            }
        }
    }

    private fun uploadTodoToRest(todo: NoteEntity) {
        webResult.text = getString(R.string.web_loading)
        lifecycleScope.launch(Dispatchers.IO) {
            val statusText = try {
                val uploaded = todoService.uploadTodo(
                    TodoUploadRequest(title = todo.content, completed = todo.completed)
                )
                restTodos.add(0, uploaded)
                getString(R.string.upload_success_format, todo.content)
            } catch (exception: Exception) {
                getString(
                    R.string.web_error_format,
                    exception.message ?: getString(R.string.unknown_error)
                )
            }

            withContext(Dispatchers.Main) {
                webResult.text = statusText
                restTodoAdapter.submitList(restTodos.toList())
                showRestMenu()
            }
        }
    }

    private fun fetchTodos() {
        webResult.text = getString(R.string.web_loading)
        lifecycleScope.launch(Dispatchers.IO) {
            val statusText = try {
                val todos = todoService.getTodos()
                restTodos.clear()
                restTodos.addAll(todos)
                getString(R.string.web_success_format, todos.size)
            } catch (exception: Exception) {
                getString(
                    R.string.web_error_format,
                    exception.message ?: getString(R.string.unknown_error)
                )
            }

            withContext(Dispatchers.Main) {
                webResult.text = statusText
                restTodoAdapter.submitList(restTodos.toList())
            }
        }
    }
}
