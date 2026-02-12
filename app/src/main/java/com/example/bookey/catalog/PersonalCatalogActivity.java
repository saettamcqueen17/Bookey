package com.example.bookey.catalog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.R;
import com.example.bookey.data.BookRepository;
import com.example.bookey.data.BookeyDatabase;
import com.example.bookey.data.PersonalBookEntity;
import com.example.bookey.data.ReadingStatus;

public class PersonalCatalogActivity extends AppCompatActivity {

    private BookRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_catalog);

        repository = new BookRepository(BookeyDatabase.getInstance(this).bookDao());

        RecyclerView recyclerView = findViewById(R.id.rvPersonalCatalog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        PersonalCatalogAdapter adapter = new PersonalCatalogAdapter(this::showEditDialog, repository::togglePersonalBook);
        recyclerView.setAdapter(adapter);

        repository.getPersonalCatalog().observe(this, adapter::submitList);
    }

    private void showEditDialog(PersonalBookEntity personalBookEntity) {
        LayoutInflater inflater = LayoutInflater.from(this);
        android.view.View dialogView = inflater.inflate(R.layout.dialog_edit_personal_book, null, false);

        EditText etRating = dialogView.findViewById(R.id.etRating);
        EditText etReview = dialogView.findViewById(R.id.etReview);
        Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);

        spinnerStatus.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Da leggere", "In lettura", "Letto"}));

        etRating.setText(String.valueOf(personalBookEntity.rating));
        etReview.setText(personalBookEntity.review);

        int selected = personalBookEntity.readingStatus == ReadingStatus.READ ? 2
                : personalBookEntity.readingStatus == ReadingStatus.READING ? 1 : 0;
        spinnerStatus.setSelection(selected);

        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_personal_book)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    int rating = 0;
                    try {
                        rating = Integer.parseInt(etRating.getText().toString());
                    } catch (NumberFormatException ignored) {
                    }
                    if (rating < 0) {
                        rating = 0;
                    }
                    if (rating > 5) {
                        rating = 5;
                    }

                    personalBookEntity.rating = rating;
                    personalBookEntity.review = etReview.getText().toString();
                    int statusIndex = spinnerStatus.getSelectedItemPosition();
                    personalBookEntity.readingStatus = statusIndex == 2 ? ReadingStatus.READ
                            : statusIndex == 1 ? ReadingStatus.READING : ReadingStatus.TO_READ;

                    repository.updatePersonalBook(personalBookEntity);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
