package com.example.bookey.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class BookEntity {

    @PrimaryKey
    @NonNull
    public String isbn;

    public String title;
    public String author;
    public String publisher;
    public String genre;
    public double price;
    public int availability;

    public BookEntity(@NonNull String isbn, String title, String author, String publisher, String genre, double price, int availability) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.price = price;
        this.availability = availability;
    }
}