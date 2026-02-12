package com.example.bookey;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookey.auth.LoginFragment;
import com.example.bookey.auth.RegisterFragment;
import com.example.bookey.menu.MainMenuActivity;

public class MainActivity extends AppCompatActivity implements LoginFragment.AuthNavigation, RegisterFragment.AuthNavigation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.authFragmentContainer, new LoginFragment())
                    .commit();
        }

        findViewById(R.id.btnShowLogin).setOnClickListener(v -> openLogin());
        findViewById(R.id.btnShowRegister).setOnClickListener(v -> openRegister());
    }

    private void openLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.authFragmentContainer, new LoginFragment())
                .commit();
    }

    private void openRegister() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.authFragmentContainer, new RegisterFragment())
                .commit();
    }

    @Override
    public void onAuthSuccess() {
        startActivity(new Intent(this, MainMenuActivity.class));
        finish();
    }

    @Override
    public void onSwitchToRegister() {
        openRegister();
    }

    @Override
    public void onSwitchToLogin() {
        openLogin();
    }
}
