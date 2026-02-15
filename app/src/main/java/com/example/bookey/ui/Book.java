package com.example.bookey.ui;

public class Book {
    public final String title;
    public final String author;
    public final String publisher;
    public final String genre;



    public Book(String title, String author, String publisher, String genre, double price, int availability) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;

    }
}
