package com.example.bookey;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.ui.Book;
import com.example.bookey.ui.BookAdapter;

import java.util.Arrays;
import java.util.List;

public class GeneralCatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_general_catalog);

        RecyclerView recyclerView = findViewById(R.id.generalCatalogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Book> books = Arrays.asList(
                new Book("Godel, Escher, Bach", "Douglas Hofstadter", "Adelphi", "ROMANZO_POLITICO", 23.00, 6),
                new Book("Il Nome della Rosa", "Umberto Eco", "Bompiani", "GIALLO", 16.50, 4),
                new Book("Norwegian Wood", "Haruki Murakami", "Einaudi", "NARRATIVA", 14.90, 9)
        );

        BookAdapter adapter = new BookAdapter(books,
                book -> Toast.makeText(this,
                        getString(R.string.book_added_to_personal_catalog, book.title),
                        Toast.LENGTH_SHORT).show());

        recyclerView.setAdapter(adapter);
    }
}
