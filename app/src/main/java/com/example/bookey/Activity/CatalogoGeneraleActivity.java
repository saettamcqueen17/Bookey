package com.example.bookey.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.Model.BookGenre;
import com.example.bookey.Model.BookPublisher;
import com.example.bookey.Entity.LibroEntity;
import com.example.bookey.Entity.UserEntity;
import com.example.bookey.R;
import com.example.bookey.data.AppDatabase;
import com.example.bookey.Entity.CatalogoPersonaleEntity;
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

    // Ricerca
    private EditText searchTitleEditText;
    private EditText searchAuthorEditText;

    // Filtri
    private Spinner genreFilterSpinner;
    private Spinner publisherFilterSpinner;

    private Button applySearchFiltersButton;
    private Button clearSearchFiltersButton;

    // Selezioni correnti
    private BookGenre selectedGenre = null;
    private BookPublisher selectedPublisher = null;

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


        recyclerView = findViewById(R.id.generalCatalogRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        searchTitleEditText = findViewById(R.id.searchTitleEditText);
        searchAuthorEditText = findViewById(R.id.searchAuthorEditText);


        genreFilterSpinner = findViewById(R.id.genreFilterSpinner);
        publisherFilterSpinner = findViewById(R.id.publisherFilterSpinner);


        applySearchFiltersButton = findViewById(R.id.applySearchFiltersButton);
        clearSearchFiltersButton = findViewById(R.id.clearSearchFiltersButton);


        setupGenreSpinner();


        setupPublisherSpinner();


        applySearchFiltersButton.setOnClickListener(v -> applySearchAndFilters());
        clearSearchFiltersButton.setOnClickListener(v -> clearAll());

        // Carica catalogo iniziale
        loadCatalog(null, null, null, null);
    }

    private void setupGenreSpinner() {
        List<String> genreOptions = new ArrayList<>();
        genreOptions.add("-- Tutti i generi --");

        //  solo i generi principali
        for (BookGenre genre : BookGenre.getMainGenres()) {
            genreOptions.add(genre.getDisplayName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genreOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreFilterSpinner.setAdapter(adapter);

        genreFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedGenre = null;
                } else {
                    String displayName = genreOptions.get(position);
                    for (BookGenre genre : BookGenre.getMainGenres()) {
                        if (genre.getDisplayName().equals(displayName)) {
                            selectedGenre = genre;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGenre = null;
            }
        });
    }

    private void setupPublisherSpinner() {
        List<String> publisherOptions = new ArrayList<>();
        publisherOptions.add("-- Tutte le case editrici --");

        for (BookPublisher publisher : BookPublisher.getAllPublishers()) {
            publisherOptions.add(publisher.getDisplayName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                publisherOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        publisherFilterSpinner.setAdapter(adapter);

        publisherFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedPublisher = null;
                } else {
                    String displayName = publisherOptions.get(position);
                    for (BookPublisher publisher : BookPublisher.getAllPublishers()) {
                        if (publisher.getDisplayName().equals(displayName)) {
                            selectedPublisher = publisher;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPublisher = null;
            }
        });
    }

    private void loadCatalog(String searchTitle, String searchAuthor,
                            BookGenre filterGenre, BookPublisher filterPublisher) {
        dbExecutor.execute(() -> {
            ensureDefaultGeneralCatalogSeeded();

            List<LibroEntity> entities;

            // Se nessun filtro/ricerca applicato
            if (searchTitle == null && searchAuthor == null &&
                filterGenre == null && filterPublisher == null) {
                entities = appDatabase.bookDao().getGeneralCatalogBooks();
            } else {
                // Applica ricerca e filtri
                String publisherName = filterPublisher != null ? filterPublisher.getDisplayName() : null;

                // Query base senza filtraggio genere gerarchico
                entities = appDatabase.bookDao().searchAndFilterBooks(
                    searchTitle,
                    searchAuthor,
                    null,  // Non usiamo questo parametro
                    publisherName
                );

                // Filtraggio genere gerarchico in memoria
                if (filterGenre != null) {
                    List<String> allowedGenres = filterGenre.getAllMatchingGenreNames();
                    List<LibroEntity> filteredByGenre = new ArrayList<>();

                    for (LibroEntity entity : entities) {
                        if (entity.genere != null) {
                            // Controllo diretto sul nome o match con gerarchia
                            for (String allowedGenre : allowedGenres) {
                                if (entity.genere.equalsIgnoreCase(allowedGenre) ||
                                    entity.genere.toUpperCase().equals(allowedGenre)) {
                                    filteredByGenre.add(entity);
                                    break;
                                }
                            }
                        }
                    }
                    entities = filteredByGenre;
                }
            }

            List<LibroUI> libroUIS = mapToUiBooks(entities);

            runOnUiThread(() -> {
                LibroAdapter adapter = new LibroAdapter(libroUIS, this::addToPersonalCatalog);
                recyclerView.setAdapter(adapter);

                if (searchTitle != null || searchAuthor != null ||
                    filterGenre != null || filterPublisher != null) {
                    String message;
                    if (libroUIS.isEmpty()) {
                        message = "Nessun libro trovato con i criteri specificati";
                    } else {
                        message = "Trovati " + libroUIS.size() + " libri";
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void applySearchAndFilters() {
        // Ricerca (testo libero)
        String searchTitle = searchTitleEditText.getText().toString().trim();
        String searchAuthor = searchAuthorEditText.getText().toString().trim();

        // Converte stringhe vuote in null
        String title = searchTitle.isEmpty() ? null : searchTitle.toLowerCase(Locale.ITALY);
        String author = searchAuthor.isEmpty() ? null : searchAuthor.toLowerCase(Locale.ITALY);

        // Controlla se c'è almeno un criterio
        if (title == null && author == null && selectedGenre == null && selectedPublisher == null) {
            Toast.makeText(this, "Inserisci almeno un criterio di ricerca o seleziona un filtro",
                          Toast.LENGTH_SHORT).show();
            return;
        }

        // Log per debug
        Log.d("SEARCH_FILTER", "Ricerca titolo: " + title);
        Log.d("SEARCH_FILTER", "Ricerca autore: " + author);
        Log.d("SEARCH_FILTER", "Filtro genere: " + (selectedGenre != null ? selectedGenre.getDisplayName() : "nessuno"));
        Log.d("SEARCH_FILTER", "Filtro editore: " + (selectedPublisher != null ? selectedPublisher.getDisplayName() : "nessuno"));

        loadCatalog(title, author, selectedGenre, selectedPublisher);
    }

    private void clearAll() {
        // Pulisci ricerca
        searchTitleEditText.setText("");
        searchAuthorEditText.setText("");

        // Reset spinner
        genreFilterSpinner.setSelection(0);
        publisherFilterSpinner.setSelection(0);

        // Reset selezioni
        selectedGenre = null;
        selectedPublisher = null;

        // Ricarica tutto
        loadCatalog(null, null, null, null);
        Toast.makeText(this, "Ricerca e filtri rimossi", Toast.LENGTH_SHORT).show();
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

            UserEntity userEntityById = appDatabase.userDao().getUserByUserId(uid);
            UserEntity userEntityByEmail = appDatabase.userDao().getUserByEmail(uid);

            Log.d("DEBUG_ADD", "userById = " + (userEntityById != null));
            Log.d("DEBUG_ADD", "userByEmail = " + (userEntityByEmail != null));


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
