package com.example.bookey.data;

import androidx.room.Embedded;
import androidx.room.Relation;

public class PersonalBookWithDetails {
    @Embedded
    public PersonalBookEntity personalBook;

    @Relation(parentColumn = "bookId", entityColumn = "id")
    public BookEntity book;
}
