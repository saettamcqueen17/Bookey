package com.example.bookey.data;

import androidx.room.TypeConverter;

public class ReadingStatusConverter {
    @TypeConverter
    public static ReadingStatus toStatus(String value) {
        if (value == null) {
            return ReadingStatus.TO_READ;
        }
        return ReadingStatus.valueOf(value);
    }

    @TypeConverter
    public static String fromStatus(ReadingStatus status) {
        return status == null ? ReadingStatus.TO_READ.name() : status.name();
    }
}
