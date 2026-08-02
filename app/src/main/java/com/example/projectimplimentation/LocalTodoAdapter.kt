package com.example.projectimplimentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectimplimentation.data.NoteEntity

class LocalTodoAdapter(
    private val onToggleComplete: (NoteEntity, Boolean) -> Unit,
    private val onDelete: (NoteEntity) -> Unit,
    private val onTransferToRest: (NoteEntity) -> Unit
) : RecyclerView.Adapter<LocalTodoAdapter.LocalTodoViewHolder>() {

    private val items = mutableListOf<NoteEntity>()

    fun submitList(todos: List<NoteEntity>) {
        items.clear()
        items.addAll(todos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalTodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_local_todo, parent, false)
        return LocalTodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalTodoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LocalTodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView: TextView = view.findViewById(R.id.localTodoTitle)
        private val completeBox: CheckBox = view.findViewById(R.id.localTodoCompleteCheckbox)
        private val deleteButton: Button = view.findViewById(R.id.localTodoDeleteButton)
        private val transferButton: Button = view.findViewById(R.id.localTodoTransferButton)

        fun bind(todo: NoteEntity) {
            titleView.text = todo.content
            completeBox.setOnCheckedChangeListener(null)
            completeBox.isChecked = todo.completed
            completeBox.setOnCheckedChangeListener { _, isChecked ->
                onToggleComplete(todo, isChecked)
            }
            deleteButton.setOnClickListener { onDelete(todo) }
            transferButton.setOnClickListener { onTransferToRest(todo) }
        }
    }
}
