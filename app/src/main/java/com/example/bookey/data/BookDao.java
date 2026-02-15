package com.example.bookey.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookDao {

    @Query("SELECT * FROM books ORDER BY title ASC")
    List<BookEntity> getGeneralCatalogBooks();

    @Query("SELECT COUNT(*) FROM books")
    int getGeneralCatalogCount();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGeneralCatalogBooks(List<BookEntity> books);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long addToPersonalCatalog(PersonalCatalogEntry entry);

    @Query("SELECT b.* FROM books b INNER JOIN personal_catalog_entries p ON p.bookIsbn = b.isbn WHERE p.userId = :userId ORDER BY b.title ASC")
    List<BookEntity> getPersonalCatalogBooks(String userId);
}