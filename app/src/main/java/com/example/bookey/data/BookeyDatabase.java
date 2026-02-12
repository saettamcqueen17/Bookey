package com.example.bookey.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {BookEntity.class, PersonalBookEntity.class}, version = 1, exportSchema = false)
@TypeConverters({ReadingStatusConverter.class})
public abstract class BookeyDatabase extends RoomDatabase {

    public abstract BookDao bookDao();

    private static volatile BookeyDatabase INSTANCE;

    public static BookeyDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BookeyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BookeyDatabase.class, "bookey_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
