package com.example.projectimplimentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectimplimentation.network.TodoResponse;

import java.util.ArrayList;
import java.util.List;

public class RestTodoAdapter extends RecyclerView.Adapter<RestTodoAdapter.RestTodoViewHolder> {

    public interface OnTransferToTodoListener {
        void onTransferToTodo(TodoResponse todo);
    }

    private final OnTransferToTodoListener onTransferToTodo;
    private final List<TodoResponse> items = new ArrayList<>();

    public RestTodoAdapter(OnTransferToTodoListener onTransferToTodo) {
        this.onTransferToTodo = onTransferToTodo;
    }

    public void submitList(List<TodoResponse> todos) {
        // Replace list wholesale to mirror server/local transfer state exactly.
        items.clear();
        items.addAll(todos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestTodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rest_todo, parent, false);
        return new RestTodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestTodoViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size;
    }

    class RestTodoViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView stateView;
        private final Button transferButton;

        RestTodoViewHolder(@NonNull View view) {
            super(view);
            titleView = view.findViewById(R.id.restTodoTitle);
            stateView = view.findViewById(R.id.restTodoState);
            transferButton = view.findViewById(R.id.restTodoTransferButton);
        }

        void bind(TodoResponse todo) {
            titleView.setText(todo.getTitle());
            stateView.setText(todo.isCompleted()
                    ? itemView.getContext().getString(R.string.completed_label)
                    : itemView.getContext().getString(R.string.pending_label));
            transferButton.setOnClickListener(v -> onTransferToTodo.onTransferToTodo(todo));
        }
    }
}
