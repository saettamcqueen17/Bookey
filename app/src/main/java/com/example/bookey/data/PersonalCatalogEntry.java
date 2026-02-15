package com.example.bookey.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "personal_catalog_entries",
        primaryKeys = {"userId", "bookIsbn"},
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = BookEntity.class,
                        parentColumns = "isbn",
                        childColumns = "bookIsbn",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"userId"}),
                @Index(value = {"bookIsbn"})
        }
)
public class PersonalCatalogEntry {

    @NonNull
    public String userId;

    @NonNull
    public String bookIsbn;

    public PersonalCatalogEntry(@NonNull String userId, @NonNull String bookIsbn) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
    }
}
