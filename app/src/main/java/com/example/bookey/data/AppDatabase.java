package com.example.bookey.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.bookey.Model.CatalogoPersonaleEntity;
import com.example.bookey.Model.LibroEntity;
import com.example.bookey.Model.User;

@Database(entities = {User.class, LibroEntity.class, CatalogoPersonaleEntity.class}, version = 4, exportSchema = false)public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract LibroDao bookDao();


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
