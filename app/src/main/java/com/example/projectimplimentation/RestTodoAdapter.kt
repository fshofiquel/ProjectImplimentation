package com.example.projectimplimentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectimplimentation.network.TodoResponse

class RestTodoAdapter(
    private val onTransferToTodo: (TodoResponse) -> Unit
) : RecyclerView.Adapter<RestTodoAdapter.RestTodoViewHolder>() {

    private val items = mutableListOf<TodoResponse>()

    fun submitList(todos: List<TodoResponse>) {
        items.clear()
        items.addAll(todos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestTodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rest_todo, parent, false)
        return RestTodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestTodoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class RestTodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView: TextView = view.findViewById(R.id.restTodoTitle)
        private val stateView: TextView = view.findViewById(R.id.restTodoState)
        private val transferButton: Button = view.findViewById(R.id.restTodoTransferButton)

        fun bind(todo: TodoResponse) {
            titleView.text = todo.title
            stateView.text = if (todo.completed) {
                itemView.context.getString(R.string.completed_label)
            } else {
                itemView.context.getString(R.string.pending_label)
            }
            transferButton.setOnClickListener { onTransferToTodo(todo) }
        }
    }
}
