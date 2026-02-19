package com.example.bookey.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "CatalogoPersonaleEntity",
        primaryKeys = {"userId", "bookIsbn"},
        foreignKeys = {
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = LibroEntity.class,
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
public class CatalogoPersonaleEntity {

    @NonNull
    public String userId;

    @NonNull
    public String bookIsbn;

    public String readingStatus; // "NON_LETTO", "IN_LETTURA", "LETTO"


    public CatalogoPersonaleEntity(@NonNull String userId, @NonNull String bookIsbn, String readingStatus) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.readingStatus = readingStatus != null ? readingStatus : "NON_LETTO";
    }
}
