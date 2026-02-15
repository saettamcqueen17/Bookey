package com.example.bookey;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.data.AppDatabase;
import com.example.bookey.data.BookEntity;
import com.example.bookey.data.PersonalCatalogEntry;
import com.example.bookey.ui.Book;
import com.example.bookey.ui.BookAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeneralCatalogActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "extra_user_id";

    private AppDatabase appDatabase;
    private ExecutorService dbExecutor;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_general_catalog);

        appDatabase = AppDatabase.getInstance(this);
        dbExecutor = Executors.newSingleThreadExecutor();
        currentUserId = getIntent().getStringExtra(EXTRA_USER_ID);

        RecyclerView recyclerView = findViewById(R.id.generalCatalogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCatalog(recyclerView);
    }

    private void loadCatalog(RecyclerView recyclerView) {
        dbExecutor.execute(() -> {
            ensureDefaultGeneralCatalogSeeded();
            List<BookEntity> entities = appDatabase.bookDao().getGeneralCatalogBooks();
            List<Book> books = mapToUiBooks(entities);

            runOnUiThread(() -> {
                BookAdapter adapter = new BookAdapter(books, this::addToPersonalCatalog);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private void ensureDefaultGeneralCatalogSeeded() {
        int count = appDatabase.bookDao().getGeneralCatalogCount();
        if (count > 0) {
            return;
        }

        List<BookEntity> initialBooks = Arrays.asList(
                new BookEntity("10000001", "Godel, Escher, Bach", "Douglas Hofstadter", "Adelphi", "ROMANZO_POLITICO", 23.00, 6),
                new BookEntity("10000002", "Il Nome della Rosa", "Umberto Eco", "Bompiani", "GIALLO", 16.50, 4),
                new BookEntity("10000003", "Norwegian Wood", "Haruki Murakami", "Einaudi", "NARRATIVA", 14.90, 9)
        );

        appDatabase.bookDao().insertGeneralCatalogBooks(initialBooks);
    }

    private List<Book> mapToUiBooks(List<BookEntity> entities) {
        List<Book> books = new ArrayList<>();
        for (BookEntity entity : entities) {
            books.add(new Book(entity.isbn, entity.title, entity.author, entity.publisher, entity.genre ));
        }
        return books;
    }

    private void addToPersonalCatalog(Book book) {
        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            Toast.makeText(this, R.string.personal_catalog_user_required, Toast.LENGTH_SHORT).show();
            return;
        }

        dbExecutor.execute(() -> {
            long result = appDatabase.bookDao().addToPersonalCatalog(new PersonalCatalogEntry(currentUserId, book.isbn));
            runOnUiThread(() -> {
                if (result == -1) {
                    Toast.makeText(this, R.string.book_already_in_personal_catalog, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.book_added_to_personal_catalog, book.title), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbExecutor.shutdown();
    }
}
