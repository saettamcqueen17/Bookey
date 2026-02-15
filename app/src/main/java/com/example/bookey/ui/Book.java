package com.example.bookey.ui;

public class Book {

    public final String isbn ;
    public final String title;
    public final String author;
    public final String publisher;
    public final String genre;



    public Book(String title, String author, String publisher, String genre, String isbn) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;

        this.isbn = isbn;
    }
}
