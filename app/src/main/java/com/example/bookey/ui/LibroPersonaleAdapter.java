package com.example.bookey.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookey.R;

import java.util.List;

public class LibroPersonaleAdapter extends RecyclerView.Adapter<LibroPersonaleAdapter.PersonalBookViewHolder> {

    public interface OnReadingStatusChange {
        void onStatusChange(LibroPersonaleUI libro, String newStatus);
    }

    private final List<LibroPersonaleUI> libriPersonali;
    private final OnReadingStatusChange onReadingStatusChange;

    public LibroPersonaleAdapter(List<LibroPersonaleUI> libriPersonali, OnReadingStatusChange onReadingStatusChange) {
        this.libriPersonali = libriPersonali;
        this.onReadingStatusChange = onReadingStatusChange;
    }

    @NonNull
    @Override
    public PersonalBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_personal_catalog, parent, false);
        return new PersonalBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalBookViewHolder holder, int position) {
        LibroPersonaleUI libro = libriPersonali.get(position);

        holder.title.setText(libro.title);
        holder.author.setText(libro.author);

        // Carica la copertina con Glide
        if (libro.coverUrl == null || libro.coverUrl.trim().isEmpty()) {
            holder.cover.setImageResource(R.drawable.catalog_book_cover_placeholder);
        } else {
            Glide.with(holder.cover)
                .load(libro.coverUrl)
                .placeholder(R.drawable.catalog_book_cover_placeholder)
                .error(R.drawable.catalog_book_cover_placeholder)
                .into(holder.cover);
        }

        // Setup toggle a 3 stati per lo stato di lettura
        updateButtonStates(holder, libro.readingStatus);

        // Listener per i tre button
        holder.notReadButton.setOnClickListener(v -> {
            libro.readingStatus = "NON_LETTO";
            updateButtonStates(holder, "NON_LETTO");
            onReadingStatusChange.onStatusChange(libro, "NON_LETTO");
        });

        holder.readingButton.setOnClickListener(v -> {
            libro.readingStatus = "IN_LETTURA";
            updateButtonStates(holder, "IN_LETTURA");
            onReadingStatusChange.onStatusChange(libro, "IN_LETTURA");
        });

        holder.readButton.setOnClickListener(v -> {
            libro.readingStatus = "LETTO";
            updateButtonStates(holder, "LETTO");
            onReadingStatusChange.onStatusChange(libro, "LETTO");
        });
    }

    private void updateButtonStates(PersonalBookViewHolder holder, String currentStatus) {
        // Reset tutti i button a stato inattivo
        resetButtonStyle(holder.notReadButton);
        resetButtonStyle(holder.readingButton);
        resetButtonStyle(holder.readButton);

        // Attiva il button corrispondente allo stato attuale
        switch (currentStatus) {
            case "NON_LETTO":
                highlightButton(holder.notReadButton);
                break;
            case "IN_LETTURA":
                highlightButton(holder.readingButton);
                break;
            case "LETTO":
                highlightButton(holder.readButton);
                break;
        }
    }

    private void highlightButton(Button button) {
        button.setBackgroundColor(0xFF6200EE); // Material Primary Blue
        button.setTextColor(0xFFFFFFFF); // White
    }

    private void resetButtonStyle(Button button) {
        button.setBackgroundColor(0xFFE8E8E8); // Light Gray
        button.setTextColor(0xFF2B2B2B); // Dark Gray
    }

    @Override
    public int getItemCount() {
        return libriPersonali.size();
    }

    static class PersonalBookViewHolder extends RecyclerView.ViewHolder {
        final ImageView cover;
        final TextView title;
        final TextView author;
        final Button notReadButton;
        final Button readingButton;
        final Button readButton;

        PersonalBookViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.personalCoverImageView);
            title = itemView.findViewById(R.id.personalTitleTextView);
            author = itemView.findViewById(R.id.personalAuthorTextView);
            notReadButton = itemView.findViewById(R.id.readingStatusNotReadButton);
            readingButton = itemView.findViewById(R.id.readingStatusReadingButton);
            readButton = itemView.findViewById(R.id.readingStatusReadButton);
        }
    }
}

