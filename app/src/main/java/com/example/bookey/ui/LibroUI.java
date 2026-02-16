package com.example.bookey.ui;

public class LibroUI {

    public final String isbn ;
    public final String title;
    public final String author;
    public final String publisher;
    public final String genre;

    public final String coverUrl;


    public LibroUI(String isbn, String title, String author, String publisher, String genre,String coverUrl) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.coverUrl= coverUrl;


    }
}
