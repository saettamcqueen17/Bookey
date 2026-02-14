package com.example.bookey;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private ExecutorService dbExecutor;
    private TextView authStatusText;
    private TextView locationText;
    private FusedLocationProviderClient fusedLocationClient;

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
    }

    private void setupAuthenticationSection() {
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText nameEditText = findViewById(R.id.nameEditText);
        Button registerButton = findViewById(R.id.registerButton);
        Button loginButton = findViewById(R.id.loginButton);
        authStatusText = findViewById(R.id.authStatusTextView);

        registerButton.setOnClickListener(v -> {
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
                        authStatusText.setText(getString(R.string.auth_register_success, displayName));
                    }
                });
            });
        });

        loginButton.setOnClickListener(v -> {
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
                        authStatusText.setText(getString(R.string.auth_login_success, user.displayName));
                    }
                });
            });
        });
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
