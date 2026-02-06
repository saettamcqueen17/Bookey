package com.example.bookey.menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookey.R;
import com.example.bookey.catalog.GeneralCatalogActivity;
import com.example.bookey.catalog.PersonalCatalogActivity;
import com.example.bookey.nearby.NearbyReadersActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        findViewById(R.id.btnPersonalCatalog).setOnClickListener(v ->
                startActivity(new Intent(this, PersonalCatalogActivity.class)));

        findViewById(R.id.btnGeneralCatalog).setOnClickListener(v ->
                startActivity(new Intent(this, GeneralCatalogActivity.class)));

        findViewById(R.id.btnNearbyReaders).setOnClickListener(v ->
                startActivity(new Intent(this, NearbyReadersActivity.class)));
    }
}
