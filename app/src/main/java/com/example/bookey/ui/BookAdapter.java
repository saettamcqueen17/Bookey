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
import java.util.Locale;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public interface OnAddBookClick {
        void onAdd(Book book);
    }

    private final List<Book> books;
    private final OnAddBookClick onAddBookClick;
    private final boolean showAddButton;

    public BookAdapter(List<Book> books, OnAddBookClick onAddBookClick) {
        this(books, onAddBookClick, true);
    }

    public BookAdapter(List<Book> books, OnAddBookClick onAddBookClick, boolean showAddButton) {
        this.books = books;
        this.onAddBookClick = onAddBookClick;
        this.showAddButton = showAddButton;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_general_catalog, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.title.setText(book.title);
        holder.author.setText(book.author);
        holder.publisher.setText(holder.itemView.getContext().getString(R.string.book_publisher, book.publisher));
        holder.genre.setText(holder.itemView.getContext().getString(R.string.book_genre, book.genre));
        holder.price.setText(holder.itemView.getContext().getString(R.string.book_price, String.format(Locale.ITALY, "%.2f", book.price)));
        holder.availability.setText(holder.itemView.getContext().getString(R.string.book_availability, book.availability));
        holder.cover.setImageResource(R.drawable.catalog_book_cover_placeholder);

        if (showAddButton) {
            holder.addButton.setVisibility(View.VISIBLE);
            holder.addButton.setOnClickListener(v -> onAddBookClick.onAdd(book));
        } else {
            holder.addButton.setVisibility(View.GONE);
            holder.addButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final ImageView cover;
        final TextView title;
        final TextView author;
        final TextView publisher;
        final TextView genre;
        final TextView price;
        final TextView availability;
        final Button addButton;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.coverImageView);
            title = itemView.findViewById(R.id.titleTextView);
            author = itemView.findViewById(R.id.authorTextView);
            publisher = itemView.findViewById(R.id.publisherTextView);
            genre = itemView.findViewById(R.id.genreTextView);
            price = itemView.findViewById(R.id.priceTextView);
            availability = itemView.findViewById(R.id.availabilityTextView);
            addButton = itemView.findViewById(R.id.addToPersonalCatalogButton);
        }
    }
}
