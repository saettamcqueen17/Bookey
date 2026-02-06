package com.example.bookey.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books ORDER BY title")
    LiveData<List<BookEntity>> getAllBooks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBooks(List<BookEntity> books);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPersonalBook(PersonalBookEntity personalBook);

    @Update
    void updatePersonalBook(PersonalBookEntity personalBook);

    @Query("DELETE FROM personal_books WHERE bookId = :bookId")
    void removePersonalBook(long bookId);

    @Query("SELECT COUNT(*) > 0 FROM personal_books WHERE bookId = :bookId")
    boolean isInPersonalCatalog(long bookId);

    @Transaction
    @Query("SELECT * FROM personal_books ORDER BY id DESC")
    LiveData<List<PersonalBookWithDetails>> getPersonalBooks();
}
