package com.example.bookey.catalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.R;
import com.example.bookey.data.BookEntity;
import com.example.bookey.data.BookRepository;

import java.util.ArrayList;
import java.util.List;

public class GeneralCatalogAdapter extends RecyclerView.Adapter<GeneralCatalogAdapter.GeneralBookViewHolder> {

    private final BookRepository repository;
    private final List<BookEntity> items = new ArrayList<>();

    public GeneralCatalogAdapter(BookRepository repository) {
        this.repository = repository;
    }

    public void submitList(List<BookEntity> books) {
        items.clear();
        if (books != null) {
            items.addAll(books);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GeneralBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_general_book, parent, false);
        return new GeneralBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralBookViewHolder holder, int position) {
        BookEntity book = items.get(position);
        holder.tvTitle.setText(book.title);
        holder.tvAuthor.setText(book.author);
        holder.tvDescription.setText(book.description);
        holder.cbPersonal.setOnCheckedChangeListener(null);
        holder.cbPersonal.setChecked(false);

        repository.isInPersonalCatalog(book.id, isSelected -> holder.itemView.post(() -> holder.cbPersonal.setChecked(isSelected)));

        holder.cbPersonal.setOnCheckedChangeListener((buttonView, isChecked) ->
                repository.togglePersonalBook(book.id, isChecked));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class GeneralBookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvDescription;
        CheckBox cbPersonal;

        public GeneralBookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvGeneralTitle);
            tvAuthor = itemView.findViewById(R.id.tvGeneralAuthor);
            tvDescription = itemView.findViewById(R.id.tvGeneralDescription);
            cbPersonal = itemView.findViewById(R.id.cbAddToPersonal);
        }
    }
}
