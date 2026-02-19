package com.example.bookey.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.bookey.Entity.CatalogoPersonaleEntity;
import com.example.bookey.Entity.LibroEntity;
import com.example.bookey.Entity.SharedBookEntity;
import com.example.bookey.Entity.UserEntity;
import com.example.bookey.Entity.UserLocationEntity;

@Database(entities = {UserEntity.class, LibroEntity.class, CatalogoPersonaleEntity.class, UserLocationEntity.class, SharedBookEntity.class}, version = 10, exportSchema = false)public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract LibroDao bookDao();
    public abstract LocationDao locationDao();


    //è il punto di accesso al database Room vero e proprio vogliamo quindi che sia
    //inequivocabilmente unico, per cui sfruttiamo il pattern Singleton
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "bookey.db"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
