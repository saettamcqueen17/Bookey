package com.example.bookey.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {

    @PrimaryKey
    @NonNull
    public String userId;

    @NonNull
    public String email;

    public String password;
    public String displayName;

    public User(@NonNull String userId, @NonNull String email, String password, String displayName) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
}
