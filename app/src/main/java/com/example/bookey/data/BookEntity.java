package com.example.bookey.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class BookEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String title;

    @NonNull
    public String author;

    @NonNull
    public String description;

    public BookEntity(@NonNull String title, @NonNull String author, @NonNull String description) {
        this.title = title;
        this.author = author;
        this.description = description;
    }
}
