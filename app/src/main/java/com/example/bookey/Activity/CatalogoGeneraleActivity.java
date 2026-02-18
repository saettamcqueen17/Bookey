package com.example.bookey.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatalogoGeneraleActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "extra_user_id";

    private AppDatabase appDatabase;
    private ExecutorService dbExecutor;
    private String currentUserId;
    private RecyclerView recyclerView;
    private EditText filterTitleEditText;
    private EditText filterAuthorEditText;
    private Button applyFiltersButton;
    private Button clearFiltersButton;

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

        // Inizializza le views
        recyclerView = findViewById(R.id.generalCatalogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        filterTitleEditText = (EditText) findViewById(R.id.filterTitleEditText);
        filterAuthorEditText = (EditText) findViewById(R.id.filterAuthorEditText);
        applyFiltersButton = findViewById(R.id.applyFiltersButton);
        clearFiltersButton = findViewById(R.id.clearFiltersButton);

        // Setup listeners per i filtri
        applyFiltersButton.setOnClickListener(v -> applyFilters());
        clearFiltersButton.setOnClickListener(v -> clearFilters());

        loadCatalog(null, null);
    }

    private void loadCatalog(String titleFilter, String authorFilter) {
        dbExecutor.execute(() -> {
            ensureDefaultGeneralCatalogSeeded();

            List<LibroEntity> entities;
            if (titleFilter == null && authorFilter == null) {
                // Nessun filtro - carica tutto
                entities = appDatabase.bookDao().getGeneralCatalogBooks();
            } else {
                // Applica filtri
                entities = appDatabase.bookDao().getFilteredGeneralCatalogBooks(titleFilter, authorFilter);
            }

            List<LibroUI> libroUIS = mapToUiBooks(entities);

            runOnUiThread(() -> {
                LibroAdapter adapter = new LibroAdapter(libroUIS, this::addToPersonalCatalog);
                recyclerView.setAdapter(adapter);

                if (titleFilter != null || authorFilter != null) {
                    Toast.makeText(this, "Trovati " + libroUIS.size() + " libri", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void applyFilters() {
        String titleFilter = filterTitleEditText.getText().toString().trim();
        String authorFilter = filterAuthorEditText.getText().toString().trim();

        // Converte stringhe vuote in null per la query SQL
        String title = titleFilter.isEmpty() ? null : titleFilter.toLowerCase(Locale.ITALY);
        String author = authorFilter.isEmpty() ? null : authorFilter.toLowerCase(Locale.ITALY);

        if (title == null && author == null) {
            Toast.makeText(this, "Inserisci almeno un criterio di ricerca", Toast.LENGTH_SHORT).show();
            return;
        }

        loadCatalog(title, author);
    }

    private void clearFilters() {
        filterTitleEditText.setText("");
        filterAuthorEditText.setText("");
        loadCatalog(null, null);
        Toast.makeText(this, "Filtri rimossi", Toast.LENGTH_SHORT).show();
    }



    //per lo sviluppo sfruttiamo questa funzione per popolare automaticamente il db Room
    // che altrimenti sui cambi di versione perderebbe le entità che lo popolano
    // e dovremmo ripopolarlo manualmente per il testing manuale.
    private void ensureDefaultGeneralCatalogSeeded() {
        int count = appDatabase.bookDao().getGeneralCatalogCount();
        if (count > 0) {
            return;
        }

        List<LibroEntity> initialBooks = Arrays.asList(
                new LibroEntity("10000001", "Godel, Escher, Bach", "Douglas Hofstadter", "Adelphi", "ROMANZO_POLITICO","https://m.media-amazon.com/images/I/51zU0Zk9zLL._SL1000_.jpg" ),
                new LibroEntity("10000002", "Il Nome della Rosa", "Umberto Eco", "Bompiani", "GIALLO","https://m.media-amazon.com/images/I/71o85G2CkyL._SL1498_.jpg" ),
                new LibroEntity("10000004","L'affaire Moro","Leonardo Sciascia","Adelphi","ROMANZO", "https://m.media-amazon.com/images/I/612Ga758sUL._SL1500_.jpg" ),
                new LibroEntity("10000003", "Norwegian Wood", "Haruki Murakami", "Einaudi", "NARRATIVA","https://www.ibs.it/images/9788806216467_0_0_536_0_75.jpg" ),
                new LibroEntity("10000005", "Una Vita Come Tante", "Hanya Yanigahara", "Feltrinelli", "ROMANZO","https://m.media-amazon.com/images/I/71dG87xBpvL._SL1500_.jpg"),
                new LibroEntity("10000006", "Un Romanzo Russo", "Emmanuele Carrere", "Feltrinelli", "ROMANZO","https://www.adelphi.it/spool/i__id7760_mw1000__1x.jpg"),
                new LibroEntity("10000007", "Il Giorno Della Civetta", "Leonardo Sciascia", "Bompiani", "GIALLO"," https://www.adelphi.it/spool/i__id10679_mw1000__1x.jpg"),
                new LibroEntity("10000008", "Memorie Dal Sottosuolo", "Fyodor Dostoevsky", "Feltrinelli", "ROMANZO","https://www.libraccio.it/images/9788863114126_0_500_0_75.jpg" )

                );

        appDatabase.bookDao().insertGeneralCatalogBooks(initialBooks);
    }

    private List<LibroUI> mapToUiBooks(List<LibroEntity> entities) {
        List<LibroUI> libroUIS = new ArrayList<>();
        for (LibroEntity entity : entities) {
            libroUIS.add(new LibroUI(entity.isbn, entity.getTitle(), entity.getAuthor(), entity.getPublisher(), entity.getCategory(), entity.coverUrl));
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


            long result = appDatabase.bookDao().addToPersonalCatalog(new CatalogoPersonaleEntity(currentUserId, libroUI.isbn, "NON_LETTO"));
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
