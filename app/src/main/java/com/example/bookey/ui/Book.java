package com.example.bookey.ui;

public class Book {
    public final String title;
    public final String author;
    public final String publisher;
    public final String genre;
    public final double price;
    public final int availability;

    public Book(String title, String author, String publisher, String genre, double price, int availability) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.genre = genre;
        this.price = price;
        this.availability = availability;
    }
}
