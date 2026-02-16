package com.example.bookey.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.R;

import java.util.List;


//utilizziamo RecyclerView per non sovraccaricare l'applicazione
// con meccanismi di caricamento manuale poco efficienti a lungo andare
public class LibroAdapter extends RecyclerView.Adapter<LibroAdapter.BookViewHolder> {

    public interface OnAddBookClick {
        void onAdd(LibroUI libroUI);
    }

    private final List<LibroUI> libroUIS;
    private final OnAddBookClick onAddBookClick;

    public LibroAdapter(List<LibroUI> libroUIS, OnAddBookClick onAddBookClick) {
        this.libroUIS = libroUIS;
        this.onAddBookClick = onAddBookClick;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_general_catalog, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        LibroUI libroUI = libroUIS.get(position);
        holder.title.setText(libroUI.title);
        holder.author.setText(libroUI.author);
        holder.publisher.setText(holder.itemView.getContext().getString(R.string.book_publisher, libroUI.publisher));
        holder.genre.setText(holder.itemView.getContext().getString(R.string.book_genre, libroUI.genre));

        holder.cover.setImageResource(R.drawable.catalog_book_cover_placeholder);
        holder.addButton.setOnClickListener(v -> onAddBookClick.onAdd(libroUI));
    }

    @Override
    public int getItemCount() {
        return libroUIS.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final ImageView cover;
        final TextView title;
        final TextView author;
        final TextView publisher;
        final TextView genre;

        final Button addButton;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.coverImageView);
            title = itemView.findViewById(R.id.titleTextView);
            author = itemView.findViewById(R.id.authorTextView);
            publisher = itemView.findViewById(R.id.publisherTextView);
            genre = itemView.findViewById(R.id.genreTextView);

            addButton = itemView.findViewById(R.id.addToPersonalCatalogButton);
        }
    }
}
