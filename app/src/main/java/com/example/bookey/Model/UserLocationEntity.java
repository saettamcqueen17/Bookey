package com.example.bookey.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "user_locations",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index(value = {"userId"}, unique = true)}
)
public class UserLocationEntity {

    @PrimaryKey
    @NonNull
    public String userId;

    public double latitude;
    public double longitude;
    public long timestamp; // timestamp dell'ultimo aggiornamento

    public UserLocationEntity(@NonNull String userId, double latitude, double longitude, long timestamp) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}

