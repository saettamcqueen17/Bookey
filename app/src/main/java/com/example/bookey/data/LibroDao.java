package com.example.bookey.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bookey.Entity.CatalogoPersonaleEntity;
import com.example.bookey.Entity.LibroEntity;

import java.util.List;

@Dao
public interface LibroDao {

    @Query("SELECT * FROM LibroEntity ORDER BY titolo ASC")
    List<LibroEntity> getGeneralCatalogBooks();

    @Query("SELECT * FROM LibroEntity ORDER BY titolo ASC")
    List<LibroEntity> getAllGeneralCatalogBooks();

    @Query("SELECT * FROM LibroEntity WHERE " +
            "(:titleFilter IS NULL OR LOWER(titolo) LIKE '%' || :titleFilter || '%') AND " +
            "(:authorFilter IS NULL OR LOWER(autore) LIKE '%' || :authorFilter || '%') " +
            "ORDER BY titolo ASC")
    List<LibroEntity> getFilteredGeneralCatalogBooks(String titleFilter, String authorFilter);

    /**
     * Query completa con ricerca (titolo/autore) e filtri (genere/editore)
     * La ricerca è case-insensitive e parziale
     * I filtri sono esatti
     */
    @Query("SELECT * FROM LibroEntity WHERE " +
            "(:searchTitle IS NULL OR LOWER(titolo) LIKE '%' || :searchTitle || '%') AND " +
            "(:searchAuthor IS NULL OR LOWER(autore) LIKE '%' || :searchAuthor || '%') AND " +
            "(:filterGenre IS NULL OR genere = :filterGenre) AND " +
            "(:filterPublisher IS NULL OR editore = :filterPublisher) " +
            "ORDER BY titolo ASC")
    List<LibroEntity> searchAndFilterBooks(String searchTitle, String searchAuthor,
                                           String filterGenre, String filterPublisher);

    /**
     * Query per generi che matchano una lista di nomi (per la gerarchia)
     */
    @Query("SELECT * FROM LibroEntity WHERE " +
            "(:searchTitle IS NULL OR LOWER(titolo) LIKE '%' || :searchTitle || '%') AND " +
            "(:searchAuthor IS NULL OR LOWER(autore) LIKE '%' || :searchAuthor || '%') AND " +
            "(:filterPublisher IS NULL OR editore = :filterPublisher) " +
            "ORDER BY titolo ASC")
    List<LibroEntity> searchAndFilterBooksWithGenreList(String searchTitle, String searchAuthor,
                                                        String filterPublisher);

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