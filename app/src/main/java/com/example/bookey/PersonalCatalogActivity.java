package com.example.bookey;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookey.data.AppDatabase;
import com.example.bookey.data.BookEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PersonalCatalogActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private ExecutorService dbExecutor;
    private TextView statusText;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_catalog);

        appDatabase = AppDatabase.getInstance(this);
        dbExecutor = Executors.newSingleThreadExecutor();
        statusText = findViewById(R.id.personalCatalogStatusTextView);
        currentUserId = getIntent().getStringExtra(GeneralCatalogActivity.EXTRA_USER_ID);

        loadPersonalCatalog();
    }

    private void loadPersonalCatalog() {
        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            statusText.setText(R.string.personal_catalog_user_required);
            return;
        }

        dbExecutor.execute(() -> {
            List<BookEntity> books = appDatabase.bookDao().getPersonalCatalogBooks(currentUserId);
            runOnUiThread(() -> renderPersonalCatalog(books));
        });
    }

    private void renderPersonalCatalog(List<BookEntity> books) {
        if (books.isEmpty()) {
            statusText.setText(R.string.personal_catalog_empty);
            return;
        }

        StringBuilder builder = new StringBuilder(getString(R.string.personal_catalog_title_list_header));
        for (BookEntity book : books) {
            builder.append("\n• ")
                    .append(book.title)
                    .append(" — ")
                    .append(book.author)
                    .append(" (ISBN ")
                    .append(book.isbn)
                    .append(")");
        }
        statusText.setText(builder.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbExecutor.shutdown();
    }
}