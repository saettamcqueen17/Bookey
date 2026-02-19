package com.example.bookey.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.bookey.Entity.UserEntity;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long register(UserEntity userEntity);

    @Query("SELECT * FROM UserEntity WHERE email = :email AND password = :password LIMIT 1")
    UserEntity login(String email, String password);

    @Query("SELECT * FROM UserEntity WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM UserEntity WHERE userId = :userId LIMIT 1")
    UserEntity getUserByUserId(String userId);
}
