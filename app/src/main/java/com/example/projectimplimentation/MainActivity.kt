package com.example.projectimplimentation

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.projectimplimentation.data.AppDatabase
import com.example.projectimplimentation.data.NoteEntity
import com.example.projectimplimentation.network.TodoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var noteInput: EditText
    private lateinit var databaseResult: TextView
    private lateinit var webResult: TextView

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

        noteInput = findViewById(R.id.noteInput)
        databaseResult = findViewById(R.id.databaseResult)
        webResult = findViewById(R.id.webResult)

        findViewById<Button>(R.id.saveNoteButton).setOnClickListener {
            saveNote()
        }

        findViewById<Button>(R.id.loadNotesButton).setOnClickListener {
            loadNotes()
        }

        findViewById<Button>(R.id.fetchTodoButton).setOnClickListener {
            fetchTodo()
        }

        loadNotes()
    }

    private fun saveNote() {
        val noteText = noteInput.text.toString().trim()
        if (noteText.isEmpty()) {
            databaseResult.text = getString(R.string.empty_note_message)
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            database.noteDao().insert(NoteEntity(content = noteText))
            withContext(Dispatchers.Main) {
                noteInput.text?.clear()
                loadNotes()
            }
        }
    }

    private fun loadNotes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val notes = database.noteDao().getAll()
            val displayText = if (notes.isEmpty()) {
                getString(R.string.no_notes_saved)
            } else {
                notes.joinToString(separator = "\n") { "• ${it.content}" }
            }

            withContext(Dispatchers.Main) {
                databaseResult.text = displayText
            }
        }
    }

    private fun fetchTodo() {
        lifecycleScope.launch(Dispatchers.IO) {
            val resultText = try {
                val todo = todoService.getSampleTodo()
                getString(R.string.web_success_format, todo.id, todo.title, todo.completed)
            } catch (exception: Exception) {
                getString(R.string.web_error_format, exception.message ?: getString(R.string.unknown_error))
            }

            withContext(Dispatchers.Main) {
                webResult.text = resultText
            }
        }
    }
}
