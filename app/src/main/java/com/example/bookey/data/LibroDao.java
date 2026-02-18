package com.example.bookey.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bookey.Model.CatalogoPersonaleEntity;
import com.example.bookey.Model.LibroEntity;

import java.util.List;

@Dao
public interface LibroDao {

    @Query("SELECT * FROM LibroEntity ORDER BY titolo ASC")
    List<LibroEntity> getGeneralCatalogBooks();

    @Query("SELECT COUNT(*) FROM LibroEntity")
    int getGeneralCatalogCount();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertGeneralCatalogBooks(List<LibroEntity> books);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long addToPersonalCatalog(CatalogoPersonaleEntity entry);

    @Query("SELECT b.* FROM LibroEntity b INNER JOIN CatalogoPersonaleEntity p ON p.bookIsbn = b.isbn WHERE p.userId = :userId ORDER BY b.titolo ASC")
    List<LibroEntity> getPersonalCatalogBooks(String userId);

    @Update
    void updatePersonalCatalogEntry(CatalogoPersonaleEntity entry);

    @Query("UPDATE CatalogoPersonaleEntity SET readingStatus = :status WHERE userId = :userId AND bookIsbn = :bookIsbn")
    void updateReadingStatus(String userId, String bookIsbn, String status);

    @Query("SELECT * FROM CatalogoPersonaleEntity WHERE userId = :currentUserId")
    List<CatalogoPersonaleEntity> getPersonalCatalogByUserId(String currentUserId);

    @Query("SELECT * FROM LibroEntity WHERE isbn = :bookIsbn LIMIT 1")
    LibroEntity getBookByIsbn(String bookIsbn);
}