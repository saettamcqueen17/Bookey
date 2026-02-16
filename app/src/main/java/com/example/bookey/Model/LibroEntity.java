package com.example.bookey.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LibroEntity")
public class LibroEntity {

    @PrimaryKey
    @NonNull
    public String isbn;

    public String titolo;
    public String autore;
    public String editore;
    public String genere;


    public LibroEntity(@NonNull String isbn, String titolo, String autore, String editore, String genere) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.editore = editore;
        this.genere = genere;

    }
}