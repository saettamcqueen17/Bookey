package com.example.bookey.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
}