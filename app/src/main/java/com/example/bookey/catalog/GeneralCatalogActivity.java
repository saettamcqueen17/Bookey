package com.example.bookey.catalog;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.R;
import com.example.bookey.data.BookRepository;
import com.example.bookey.data.BookeyDatabase;

public class GeneralCatalogActivity extends AppCompatActivity {

    private BookRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_catalog);

        repository = new BookRepository(BookeyDatabase.getInstance(this).bookDao());
        repository.seedGeneralCatalogIfNeeded();

        RecyclerView recyclerView = findViewById(R.id.rvGeneralCatalog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GeneralCatalogAdapter adapter = new GeneralCatalogAdapter(repository);
        recyclerView.setAdapter(adapter);

        repository.getGeneralCatalog().observe(this, adapter::submitList);
    }
}
