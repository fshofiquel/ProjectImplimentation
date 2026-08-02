package com.example.projectimplimentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectimplimentation.data.NoteEntity;

import java.util.ArrayList;
import java.util.List;

public class LocalTodoAdapter extends RecyclerView.Adapter<LocalTodoAdapter.LocalTodoViewHolder> {

    public interface OnToggleCompleteListener {
        void onToggleComplete(NoteEntity todo, boolean completed);
    }

    public interface OnDeleteListener {
        void onDelete(NoteEntity todo);
    }

    public interface OnTransferToRestListener {
        void onTransferToRest(NoteEntity todo);
    }

    private final OnToggleCompleteListener onToggleComplete;
    private final OnDeleteListener onDelete;
    private final OnTransferToRestListener onTransferToRest;
    private final List<NoteEntity> items = new ArrayList<>();

    public LocalTodoAdapter(OnToggleCompleteListener onToggleComplete,
                            OnDeleteListener onDelete,
                            OnTransferToRestListener onTransferToRest) {
        this.onToggleComplete = onToggleComplete;
        this.onDelete = onDelete;
        this.onTransferToRest = onTransferToRest;
    }

    public void submitList(List<NoteEntity> todos) {
        items.clear();
        items.addAll(todos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocalTodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_local_todo, parent, false);
        return new LocalTodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalTodoViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size;
    }

    class LocalTodoViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final CheckBox completeBox;
        private final Button deleteButton;
        private final Button transferButton;

        LocalTodoViewHolder(@NonNull View view) {
            super(view);
            titleView = view.findViewById(R.id.localTodoTitle);
            completeBox = view.findViewById(R.id.localTodoCompleteCheckbox);
            deleteButton = view.findViewById(R.id.localTodoDeleteButton);
            transferButton = view.findViewById(R.id.localTodoTransferButton);
        }

        void bind(NoteEntity todo) {
            titleView.setText(todo.getContent());
            completeBox.setOnCheckedChangeListener(null);
            completeBox.setChecked(todo.isCompleted());
            completeBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    onToggleComplete.onToggleComplete(todo, isChecked));
            deleteButton.setOnClickListener(v -> onDelete.onDelete(todo));
            transferButton.setOnClickListener(v -> onTransferToRest.onTransferToRest(todo));
        }
    }
}
