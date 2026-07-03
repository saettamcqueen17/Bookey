package com.example.bookey.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    public interface OnFriendClickListener {
        void onOpenChat(String friendUserId, String friendName);
    }

    private final List<String[]> friends; // ogni entry: [userId, displayName]
    private final OnFriendClickListener listener;

    public FriendListAdapter(List<String[]> friends, OnFriendClickListener listener) {
        this.friends = friends;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] friend = friends.get(position);
        String userId = friend[0];
        String name = friend[1];

        holder.friendNameTextView.setText(name);
        holder.openChatButton.setOnClickListener(v -> {
            if (listener != null) listener.onOpenChat(userId, name);
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView;
        MaterialButton openChatButton;

        ViewHolder(View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.friendNameTextView);
            openChatButton = itemView.findViewById(R.id.openChatButton);
        }
    }
}

