package com.example.bookey.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.Model.CatalogoPersonaleEntity;
import com.example.bookey.Model.LibroEntity;
import com.example.bookey.R;
import com.example.bookey.data.AppDatabase;
import com.example.bookey.ui.LibroPersonaleUI;
import com.example.bookey.ui.LibroPersonaleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatalogoPersonaleActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private ExecutorService dbExecutor;
    private TextView statusText;
    private RecyclerView recyclerView;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_catalog);

        appDatabase = AppDatabase.getInstance(this);
        dbExecutor = Executors.newSingleThreadExecutor();
        statusText = findViewById(R.id.personalCatalogStatusTextView);
        recyclerView = findViewById(R.id.personalCatalogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        currentUserId = getIntent().getStringExtra(CatalogoGeneraleActivity.EXTRA_USER_ID);

        loadPersonalCatalog();
    }

    private void loadPersonalCatalog() {
        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            statusText.setText(R.string.personal_catalog_user_required);
            return;
        }

        dbExecutor.execute(() -> {
            List<LibroEntity> books = appDatabase.bookDao().getPersonalCatalogBooks(currentUserId);
            runOnUiThread(() -> renderPersonalCatalog(books));
        });
    }

    private void renderPersonalCatalog(List<LibroEntity> books) {
        if (books.isEmpty()) {
            statusText.setText(R.string.personal_catalog_empty);
            recyclerView.setVisibility(android.view.View.GONE);
            return;
        }

        statusText.setVisibility(android.view.View.GONE);
        recyclerView.setVisibility(android.view.View.VISIBLE);

        List<LibroPersonaleUI> libriUI = mapToPersonalUiBooks(books);
        LibroPersonaleAdapter adapter = new LibroPersonaleAdapter(libriUI, this::updateReadingStatus);
        recyclerView.setAdapter(adapter);
    }

    private List<LibroPersonaleUI> mapToPersonalUiBooks(List<LibroEntity> entities) {
        List<LibroPersonaleUI> libriUI = new ArrayList<>();
        for (LibroEntity entity : entities) {
            libriUI.add(new LibroPersonaleUI(
                entity.isbn,
                entity.titolo,
                entity.autore,
                entity.coverUrl,
                "NON_LETTO"
            ));
        }
        return libriUI;
    }

    private void updateReadingStatus(LibroPersonaleUI libro, String newStatus) {
        dbExecutor.execute(() -> {
            CatalogoPersonaleEntity entry = new CatalogoPersonaleEntity(currentUserId, libro.isbn, newStatus);
            appDatabase.bookDao().updateReadingStatus(currentUserId, libro.isbn, newStatus);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbExecutor.shutdown();
    }
}