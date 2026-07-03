package com.example.bookey.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.Entity.FriendRequestEntity;
import com.example.bookey.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Map;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    public interface OnRequestActionListener {
        void onAccept(FriendRequestEntity request);
        void onReject(FriendRequestEntity request);
    }

    private final List<FriendRequestEntity> requests;
    private final Map<String, String> userNames; // userId -> displayName
    private final OnRequestActionListener listener;

    public FriendRequestAdapter(List<FriendRequestEntity> requests,
                                 Map<String, String> userNames,
                                 OnRequestActionListener listener) {
        this.requests = requests;
        this.userNames = userNames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequestEntity request = requests.get(position);

        String name = userNames.containsKey(request.fromUserId)
                ? userNames.get(request.fromUserId) : request.fromUserId;
        holder.requesterNameTextView.setText(name);
        holder.requestStatusTextView.setText("Vuole aggiungerti come amico");

        holder.acceptButton.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(request);
        });

        holder.rejectButton.setOnClickListener(v -> {
            if (listener != null) listener.onReject(request);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView requesterNameTextView;
        TextView requestStatusTextView;
        MaterialButton acceptButton;
        MaterialButton rejectButton;

        ViewHolder(View itemView) {
            super(itemView);
            requesterNameTextView = itemView.findViewById(R.id.requesterNameTextView);
            requestStatusTextView = itemView.findViewById(R.id.requestStatusTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}

