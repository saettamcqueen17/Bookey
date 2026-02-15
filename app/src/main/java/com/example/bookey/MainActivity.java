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
import androidx.core.app.ActivityCompat;
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

    private TextInputLayout userIdInputLayout;
    private EditText userIdEditText;
    private EditText emailEditText;
    private EditText passwordEditText;



    private Button loginButton;
    private Button registerButton;
    private Button openGeneralCatalogButton;
    private Button openPersonalCatalogButton;
    private Button openMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        appDatabase = AppDatabase.getInstance(this);
        dbExecutor = Executors.newSingleThreadExecutor();

        setupAuthenticationSection();

        setupMenuSection();
    }

    private void setupAuthenticationSection() {
        userIdInputLayout = findViewById(R.id.userIdInputLayout);
        userIdEditText = findViewById(R.id.userIdEditText);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        authTitleText = findViewById(R.id.authTitleTextView);
        authSubtitleText = findViewById(R.id.authSubtitleTextView);
        authStatusText = findViewById(R.id.authStatusTextView);

        authTitleText.setText(R.string.auth_login_title);
        authSubtitleText.setText(R.string.auth_login_subtitle);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.auth_fill_email_password, Toast.LENGTH_SHORT).show();
            return;
        }

        dbExecutor.execute(() -> {
            User existingUser = appDatabase.userDao().getUserByEmail(email);
            if (existingUser != null) {
                runOnUiThread(() -> authStatusText.setText(getString(R.string.auth_user_exists, email)));
                return;
            }

            String displayName = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
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
