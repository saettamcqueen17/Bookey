package com.example.bookey.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "shared_books",
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
public class SharedBookEntity {

    @NonNull
    public String userId;

    @NonNull
    public String bookIsbn;

    public int displayOrder; // ordine di visualizzazione (1-5)
    public boolean isManuallySelected; // true se selezionato dall'utente, false se casuale

    public SharedBookEntity(@NonNull String userId, @NonNull String bookIsbn, int displayOrder, boolean isManuallySelected) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.displayOrder = displayOrder;
        this.isManuallySelected = isManuallySelected;
    }
}

