package com.example.bookey.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.bookey.Entity.SharedBookEntity;
import com.example.bookey.Entity.UserLocationEntity;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateLocation(UserLocationEntity location);

    @Query("SELECT * FROM user_locations WHERE userId = :userId LIMIT 1")
    UserLocationEntity getLocationByUserId(String userId);

    @Query("SELECT * FROM user_locations WHERE userId != :excludeUserId")
    List<UserLocationEntity> getAllLocationsExcept(String excludeUserId);

    @Query("DELETE FROM user_locations WHERE userId = :userId")
    void deleteLocationByUserId(String userId);

    // Shared books queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSharedBook(SharedBookEntity sharedBook);

    @Query("DELETE FROM shared_books WHERE userId = :userId")
    void deleteAllSharedBooksByUser(String userId);

    @Query("SELECT * FROM shared_books WHERE userId = :userId ORDER BY displayOrder ASC")
    List<SharedBookEntity> getSharedBooksByUserId(String userId);
}

