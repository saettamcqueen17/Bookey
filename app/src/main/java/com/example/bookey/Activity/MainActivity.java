package com.example.bookey.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookey.R;
import com.example.bookey.data.AppDatabase;
import com.example.bookey.Model.User;
import com.google.android.material.textfield.TextInputLayout;

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
    private String authenticatedUserId;

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

        loginButton.setOnClickListener(v -> {
            userIdInputLayout.setVisibility(android.view.View.GONE);
            userIdEditText.setText("");
            loginUser();
        });
        registerButton.setOnClickListener(v -> {
            if (userIdInputLayout.getVisibility() != android.view.View.VISIBLE) {
                userIdInputLayout.setVisibility(android.view.View.VISIBLE);
                authStatusText.setText(R.string.auth_register_user_id_prompt);
                return;
            }
            registerUser();
        });
    }

    private void registerUser() {
        String userId = userIdEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (userId.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.auth_fill_registration_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        dbExecutor.execute(() -> {
            User existingUserByEmail = appDatabase.userDao().getUserByEmail(email);
            if (existingUserByEmail != null) {
                runOnUiThread(() -> authStatusText.setText(getString(R.string.auth_user_exists, email)));
                return;
            }

            User existingUserByUserId = appDatabase.userDao().getUserByUserId(userId);
            if (existingUserByUserId != null) {
                runOnUiThread(() -> authStatusText.setText(getString(R.string.auth_user_id_exists, userId)));
                return;
            }

            String displayName = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            long result = appDatabase.userDao().register(new User(userId, email, password, displayName));
            runOnUiThread(() -> {
                if (result == -1) {
                    authStatusText.setText(getString(R.string.auth_user_exists, email));
                } else {
                    onAuthenticationSuccess(userId, displayName, true);                }
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
                    onAuthenticationSuccess(user.userId, user.displayName, false);
                }
            });
        });
    }

    private void onAuthenticationSuccess(String userId, String displayName, boolean fromRegistration) {
        authenticatedUserId = userId;
        {
            if (fromRegistration) {
                authStatusText.setText(getString(R.string.auth_register_success, displayName));
            } else {
                authStatusText.setText(getString(R.string.auth_login_success, displayName));
            }

            findViewById(R.id.authContainer).setVisibility(android.view.View.GONE);
            findViewById(R.id.menuContainer).setVisibility(android.view.View.VISIBLE);
            menuWelcomeText.setText(getString(R.string.menu_welcome_message, displayName));
        }
    }
    private void setupMenuSection() {
        menuWelcomeText = findViewById(R.id.menuWelcomeTextView);
        openGeneralCatalogButton = findViewById(R.id.openGeneralCatalogButton);
        openPersonalCatalogButton = findViewById(R.id.openPersonalCatalogButton);
        openMapButton = findViewById(R.id.openMapButton);

        openGeneralCatalogButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CatalogoGeneraleActivity.class);
            intent.putExtra(CatalogoGeneraleActivity.EXTRA_USER_ID, authenticatedUserId);
            startActivity(intent);
        });
        openPersonalCatalogButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CatalogoPersonaleActivity.class);
            intent.putExtra(CatalogoGeneraleActivity.EXTRA_USER_ID, authenticatedUserId);
            startActivity(intent);
        });
        openMapButton.setOnClickListener(v ->
                startActivity(new Intent(this, MappaActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbExecutor.shutdown();
    }
}



