package com.example.bookey.ui;

public class LibroPersonaleUI {

    public final String isbn;
    public final String title;
    public final String author;
    public final String coverUrl;
    public String readingStatus; // "NON_LETTO", "IN_LETTURA", "LETTO"

    public LibroPersonaleUI(String isbn, String title, String author, String coverUrl, String readingStatus) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
        this.readingStatus = readingStatus != null ? readingStatus : "NON_LETTO";
    }
}

