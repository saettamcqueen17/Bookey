package com.example.bookey.Model;

import com.example.bookey.Entity.LibroEntity;

import java.util.List;

public class UserMapData {
    public String userId;
    public String displayName;
    public double latitude;
    public double longitude;
    public List<LibroEntity> sharedBooks;

    public UserMapData(String userId, String displayName, double latitude, double longitude, List<LibroEntity> sharedBooks) {
        this.userId = userId;
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sharedBooks = sharedBooks;
    }
}

