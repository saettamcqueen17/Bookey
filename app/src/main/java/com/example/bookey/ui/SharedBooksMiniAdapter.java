package com.example.bookey.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.Entity.LibroEntity;
import com.example.bookey.R;

import java.util.List;

public class SharedBooksMiniAdapter extends RecyclerView.Adapter<SharedBooksMiniAdapter.ViewHolder> {

    private final List<LibroEntity> books;

    public SharedBooksMiniAdapter(List<LibroEntity> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shared_book_mini, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibroEntity book = books.get(position);
        String info = "• " + book.titolo + " - " + book.autore;
        holder.bookInfoTextView.setText(info);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookInfoTextView;

        ViewHolder(View itemView) {
            super(itemView);
            bookInfoTextView = itemView.findViewById(R.id.bookInfoTextView);
        }
    }
}

