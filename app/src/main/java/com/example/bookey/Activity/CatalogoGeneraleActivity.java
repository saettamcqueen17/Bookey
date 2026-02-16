package com.example.bookey.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.Model.LibroEntity;
import com.example.bookey.Model.User;
import com.example.bookey.R;
import com.example.bookey.data.AppDatabase;
import com.example.bookey.Model.CatalogoPersonaleEntity;
import com.example.bookey.ui.LibroUI;
import com.example.bookey.ui.LibroAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatalogoGeneraleActivity extends AppCompatActivity {

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
        if (currentUserId != null) {
            currentUserId = currentUserId.trim();
        }
        RecyclerView recyclerView = findViewById(R.id.generalCatalogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadCatalog(recyclerView);
    }

    private void loadCatalog(RecyclerView recyclerView) {
        dbExecutor.execute(() -> {
            ensureDefaultGeneralCatalogSeeded();
            List<LibroEntity> entities = appDatabase.bookDao().getGeneralCatalogBooks();
            List<LibroUI> libroUIS = mapToUiBooks(entities);

            runOnUiThread(() -> {
                LibroAdapter adapter = new LibroAdapter(libroUIS, this::addToPersonalCatalog);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private void ensureDefaultGeneralCatalogSeeded() {
        int count = appDatabase.bookDao().getGeneralCatalogCount();
        if (count > 0) {
            return;
        }

        List<LibroEntity> initialBooks = Arrays.asList(
                new LibroEntity("10000001", "Godel, Escher, Bach", "Douglas Hofstadter", "Adelphi", "ROMANZO_POLITICO","https://m.media-amazon.com/images/I/51zU0Zk9zLL._SL1000_.jpg" ),
                new LibroEntity("10000002", "Il Nome della Rosa", "Umberto Eco", "Bompiani", "GIALLO","https://m.media-amazon.com/images/I/71o85G2CkyL._SL1498_.jpg" ),
                new LibroEntity("10000003", "Norwegian Wood", "Haruki Murakami", "Einaudi", "NARRATIVA","https://www.ibs.it/images/9788806216467_0_0_536_0_75.jpg" )
        );

        appDatabase.bookDao().insertGeneralCatalogBooks(initialBooks);
    }

    private List<LibroUI> mapToUiBooks(List<LibroEntity> entities) {
        List<LibroUI> libroUIS = new ArrayList<>();
        for (LibroEntity entity : entities) {
            libroUIS.add(new LibroUI(entity.isbn, entity.titolo, entity.autore, entity.editore, entity.genere, entity.coverUrl));
        }
        return libroUIS;
    }

    private void addToPersonalCatalog(LibroUI libroUI) {
        if (currentUserId == null || currentUserId.isEmpty()) {            Toast.makeText(this, R.string.personal_catalog_user_required, Toast.LENGTH_SHORT).show();
            return;
        }

        dbExecutor.execute(() -> {

            String uid = currentUserId == null ? null : currentUserId.trim();

            Log.d("DEBUG_ADD", "uid raw = [" + currentUserId + "]");
            Log.d("DEBUG_ADD", "uid trimmed = [" + uid + "]");
            Log.d("DEBUG_ADD", "isbn = [" + libroUI.isbn + "]");

            User userById = appDatabase.userDao().getUserByUserId(uid);
            User userByEmail = appDatabase.userDao().getUserByEmail(uid);

            Log.d("DEBUG_ADD", "userById = " + (userById != null));
            Log.d("DEBUG_ADD", "userByEmail = " + (userByEmail != null));


            long result = appDatabase.bookDao().addToPersonalCatalog(new CatalogoPersonaleEntity(currentUserId, libroUI.isbn));
            runOnUiThread(() -> {
                if (result == -1) {
                    Toast.makeText(this, R.string.book_already_in_personal_catalog, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.book_added_to_personal_catalog, libroUI.title), Toast.LENGTH_SHORT).show();
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
