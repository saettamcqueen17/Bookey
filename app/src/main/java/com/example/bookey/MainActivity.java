package com.example.bookey;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.data.AppDatabase;
import com.example.bookey.data.User;
import com.example.bookey.ui.Book;
import com.example.bookey.ui.BookAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private ExecutorService dbExecutor;
    private TextView authStatusText;
    private TextView authTitleText;
    private TextView authSubtitleText;
    private TextView menuWelcomeText;
    private TextView locationText;
    private TextInputLayout nameInputLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private Button authSubmitButton;
    private Button authModeSwitchButton;
    private View menuContainer;
    private View generalCatalogSection;
    private View personalCatalogSection;
    private View locationSection;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isLoginMode = true;

    private final ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    fetchDeviceLocation();
                } else {
                    locationText.setText(R.string.location_permission_denied);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        appDatabase = AppDatabase.getInstance(this);
        dbExecutor = Executors.newSingleThreadExecutor();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupAuthenticationSection();
        setupCatalogSection();
        setupLocationSection();
        setupMenuSection();
    }

    private void setupAuthenticationSection() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        nameInputLayout = findViewById(R.id.nameInputLayout);
        authSubmitButton = findViewById(R.id.authSubmitButton);
        authModeSwitchButton = findViewById(R.id.authModeSwitchButton);
        authTitleText = findViewById(R.id.authTitleTextView);
        authSubtitleText = findViewById(R.id.authSubtitleTextView);
        authStatusText = findViewById(R.id.authStatusTextView);

        authSubmitButton.setOnClickListener(v -> {
            if (isLoginMode) {
                loginUser();
            } else {
                registerUser();
            }
        });

        authModeSwitchButton.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            applyAuthModeUi();
        });

        applyAuthModeUi();
    }

    private void applyAuthModeUi() {
        if (isLoginMode) {
            authTitleText.setText(R.string.auth_login_title);
            authSubtitleText.setText(R.string.auth_login_subtitle);
            authSubmitButton.setText(R.string.auth_login_action);
            authModeSwitchButton.setText(R.string.auth_switch_to_register);
            nameInputLayout.setVisibility(View.GONE);
        } else {
            authTitleText.setText(R.string.auth_register_title);
            authSubtitleText.setText(R.string.auth_register_subtitle);
            authSubmitButton.setText(R.string.auth_register_action);
            authModeSwitchButton.setText(R.string.auth_switch_to_login);
            nameInputLayout.setVisibility(View.VISIBLE);
        }
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String displayName = nameEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
            Toast.makeText(this, R.string.auth_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        dbExecutor.execute(() -> {
            long result = appDatabase.userDao().register(new User(email, password, displayName));
            runOnUiThread(() -> {
                if (result == -1) {
                    authStatusText.setText(getString(R.string.auth_user_exists, email));
                } else {
                    onAuthenticationSuccess(displayName, true);
                }
            });
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.auth_fill_email_password, Toast.LENGTH_SHORT).show();
            return;
        }

        dbExecutor.execute(() -> {
            User user = appDatabase.userDao().login(email, password);
            runOnUiThread(() -> {
                if (user == null) {
                    authStatusText.setText(R.string.auth_login_failed);
                } else {
                    onAuthenticationSuccess(user.displayName, false);
                }
            });
        });
    }

    private void onAuthenticationSuccess(String displayName, boolean fromRegistration) {
        if (fromRegistration) {
            authStatusText.setText(getString(R.string.auth_register_success, displayName));
        } else {
            authStatusText.setText(getString(R.string.auth_login_success, displayName));
        }

        findViewById(R.id.authContainer).setVisibility(View.GONE);
        menuContainer.setVisibility(View.VISIBLE);
        menuWelcomeText.setText(getString(R.string.menu_welcome_message, displayName));
        showOnlySection(null);
    }

    private void setupMenuSection() {
        menuContainer = findViewById(R.id.menuContainer);
        menuWelcomeText = findViewById(R.id.menuWelcomeTextView);
        generalCatalogSection = findViewById(R.id.generalCatalogSection);
        personalCatalogSection = findViewById(R.id.personalCatalogSection);
        locationSection = findViewById(R.id.locationSection);

        Button openGeneralCatalogButton = findViewById(R.id.openGeneralCatalogButton);
        Button openPersonalCatalogButton = findViewById(R.id.openPersonalCatalogButton);
        Button openMapButton = findViewById(R.id.openMapButton);

        openGeneralCatalogButton.setOnClickListener(v -> showOnlySection(generalCatalogSection));
        openPersonalCatalogButton.setOnClickListener(v -> showOnlySection(personalCatalogSection));
        openMapButton.setOnClickListener(v -> showOnlySection(locationSection));
    }

    private void showOnlySection(View sectionToShow) {
        generalCatalogSection.setVisibility(sectionToShow == generalCatalogSection ? View.VISIBLE : View.GONE);
        personalCatalogSection.setVisibility(sectionToShow == personalCatalogSection ? View.VISIBLE : View.GONE);
        locationSection.setVisibility(sectionToShow == locationSection ? View.VISIBLE : View.GONE);
    }

    private void setupCatalogSection() {
        RecyclerView generalCatalogRecyclerView = findViewById(R.id.generalCatalogRecyclerView);
        generalCatalogRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView personalCatalogRecyclerView = findViewById(R.id.personalCatalogRecyclerView);
        personalCatalogRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        personalCatalogStatusText = findViewById(R.id.personalCatalogStatusTextView);

        List<Book> books = Arrays.asList(
                new Book("Godel, Escher, Bach", "Douglas Hofstadter", "Adelphi", "ROMANZO_POLITICO", 23.00, 6),
                new Book("Il Nome della Rosa", "Umberto Eco", "Bompiani", "GIALLO", 16.50, 4),
                new Book("Norwegian Wood", "Haruki Murakami", "Einaudi", "NARRATIVA", 14.90, 9)
        );

        BookAdapter generalCatalogAdapter = new BookAdapter(books,
                book -> {
                    if (isAlreadyInPersonalCatalog(book)) {
                        Toast.makeText(this, R.string.personal_catalog_duplicate, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    personalCatalogBooks.add(book);
                    personalCatalogAdapter.notifyItemInserted(personalCatalogBooks.size() - 1);
                    updatePersonalCatalogStatus();

                    Toast.makeText(this,
                            getString(R.string.book_added_to_personal_catalog, book.title),
                            Toast.LENGTH_SHORT).show();
                });

        personalCatalogAdapter = new BookAdapter(personalCatalogBooks, book -> {
        }, false);

        generalCatalogRecyclerView.setAdapter(generalCatalogAdapter);
        personalCatalogRecyclerView.setAdapter(personalCatalogAdapter);
        updatePersonalCatalogStatus();
    }

    private boolean isAlreadyInPersonalCatalog(Book candidate) {
        for (Book book : personalCatalogBooks) {
            if (book.title.equals(candidate.title) && book.author.equals(candidate.author)) {
                return true;
            }
        }
        return false;
    }

    private void updatePersonalCatalogStatus() {
        if (personalCatalogBooks.isEmpty()) {
            personalCatalogStatusText.setText(R.string.personal_catalog_empty);
        } else {
            personalCatalogStatusText.setText(getString(R.string.personal_catalog_count, personalCatalogBooks.size()));
        }
    }

    private void setupLocationSection() {
        Button requestLocationButton = findViewById(R.id.requestLocationButton);
        locationText = findViewById(R.id.locationStatusTextView);
        requestLocationButton.setOnClickListener(v -> checkLocationPermissionAndFetch());
    }

    private void checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchDeviceLocation();
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchDeviceLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, this::handleLocation)
                .addOnFailureListener(e -> locationText.setText(getString(R.string.location_error, e.getMessage())));
    }

    private void handleLocation(Location location) {
        if (location == null) {
            locationText.setText(R.string.location_not_available);
            return;
        }

        String message = String.format(
                Locale.ITALY,
                getString(R.string.location_coordinates),
                location.getLatitude(),
                location.getLongitude()
        );
        locationText.setText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbExecutor.shutdown();
    }
}
