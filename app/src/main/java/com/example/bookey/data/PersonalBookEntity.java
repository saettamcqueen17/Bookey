package com.example.bookey.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "personal_books",
        foreignKeys = @ForeignKey(
                entity = BookEntity.class,
                parentColumns = "id",
                childColumns = "bookId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("bookId")}
)
public class PersonalBookEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long bookId;

    public int rating;

    public String review;

    public ReadingStatus readingStatus;

    public PersonalBookEntity(long bookId, int rating, String review, ReadingStatus readingStatus) {
        this.bookId = bookId;
        this.rating = rating;
        this.review = review;
        this.readingStatus = readingStatus;
    }
}
