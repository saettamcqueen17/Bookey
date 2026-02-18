package com.example.bookey.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectableBookAdapter extends RecyclerView.Adapter<SelectableBookAdapter.ViewHolder> {

    private final List<LibroPersonaleUI> books;
    private final Set<String> selectedIsbns;
    private final int maxSelection;

    public SelectableBookAdapter(List<LibroPersonaleUI> books, int maxSelection) {
        this.books = books;
        this.maxSelection = maxSelection;
        this.selectedIsbns = new HashSet<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_selectable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibroPersonaleUI book = books.get(position);
        holder.titleTextView.setText(book.title);
        holder.authorTextView.setText(book.author);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedIsbns.contains(book.isbn));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (selectedIsbns.size() < maxSelection) {
                    selectedIsbns.add(book.isbn);
                } else {
                    buttonView.setChecked(false);
                }
            } else {
                selectedIsbns.remove(book.isbn);
            }
        });

        holder.itemView.setOnClickListener(v -> holder.checkBox.performClick());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public List<String> getSelectedIsbns() {
        return new ArrayList<>(selectedIsbns);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView titleTextView;
        TextView authorTextView;

        ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.bookCheckBox);
            titleTextView = itemView.findViewById(R.id.bookTitleTextView);
            authorTextView = itemView.findViewById(R.id.bookAuthorTextView);
        }
    }
}

