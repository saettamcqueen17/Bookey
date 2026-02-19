package com.example.bookey.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class UserEntity {

    @PrimaryKey
    @NonNull
    public String userId;

    @NonNull
    public String email;

    public String password;
    public String displayName;

    public UserEntity(@NonNull String userId, @NonNull String email, String password, String displayName) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
}
