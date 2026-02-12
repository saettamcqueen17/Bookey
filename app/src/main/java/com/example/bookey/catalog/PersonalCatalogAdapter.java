package com.example.bookey.catalog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.R;
import com.example.bookey.data.PersonalBookEntity;
import com.example.bookey.data.PersonalBookWithDetails;

import java.util.ArrayList;
import java.util.List;

public class PersonalCatalogAdapter extends RecyclerView.Adapter<PersonalCatalogAdapter.PersonalBookViewHolder> {

    public interface OnEditClickListener {
        void onEdit(PersonalBookEntity personalBookEntity);
    }

    public interface OnRemoveClickListener {
        void onRemove(long bookId, boolean selected);
    }

    private final List<PersonalBookWithDetails> items = new ArrayList<>();
    private final OnEditClickListener onEditClickListener;
    private final OnRemoveClickListener onRemoveClickListener;

    public PersonalCatalogAdapter(OnEditClickListener onEditClickListener,
                                  OnRemoveClickListener onRemoveClickListener) {
        this.onEditClickListener = onEditClickListener;
        this.onRemoveClickListener = onRemoveClickListener;
    }

    public void submitList(List<PersonalBookWithDetails> books) {
        items.clear();
        if (books != null) {
            items.addAll(books);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonalBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_book, parent, false);
        return new PersonalBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalBookViewHolder holder, int position) {
        PersonalBookWithDetails item = items.get(position);
        holder.tvTitle.setText(item.book.title);
        holder.tvAuthor.setText(item.book.author);
        holder.tvRating.setText(holder.itemView.getContext().getString(R.string.rating_template, item.personalBook.rating));
        holder.tvStatus.setText(item.personalBook.readingStatus.name());
        holder.tvReview.setText(item.personalBook.review == null || item.personalBook.review.isEmpty()
                ? holder.itemView.getContext().getString(R.string.no_review)
                : item.personalBook.review);

        holder.btnEdit.setOnClickListener(v -> onEditClickListener.onEdit(item.personalBook));
        holder.btnRemove.setOnClickListener(v -> onRemoveClickListener.onRemove(item.book.id, false));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PersonalBookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvRating;
        TextView tvStatus;
        TextView tvReview;
        ImageButton btnEdit;
        ImageButton btnRemove;

        public PersonalBookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPersonalTitle);
            tvAuthor = itemView.findViewById(R.id.tvPersonalAuthor);
            tvRating = itemView.findViewById(R.id.tvPersonalRating);
            tvStatus = itemView.findViewById(R.id.tvPersonalStatus);
            tvReview = itemView.findViewById(R.id.tvPersonalReview);
            btnEdit = itemView.findViewById(R.id.btnEditPersonal);
            btnRemove = itemView.findViewById(R.id.btnRemovePersonal);
        }
    }
}
