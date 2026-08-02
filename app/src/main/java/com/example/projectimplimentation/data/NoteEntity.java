package com.example.projectimplimentation.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

// Room entity used by the local TODO list.
@Entity(tableName = "notes")
public class NoteEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String content;

    private boolean completed;

    public NoteEntity(int id, @NonNull String content, boolean completed) {
        this.id = id;
        this.content = content;
        this.completed = completed;
    }

    @Ignore
    public NoteEntity(@NonNull String content, boolean completed) {
        this(0, content, completed);
    }

    @Ignore
    public NoteEntity(@NonNull String content) {
        this(0, content, false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
