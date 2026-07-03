package com.example.bookey.ui;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.Entity.MessageEntity;
import com.example.bookey.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final List<MessageEntity> messages;
    private final String currentUserId;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ITALY);

    public MessageAdapter(List<MessageEntity> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageEntity message = messages.get(position);

        holder.messageTextView.setText(message.messageText);
        holder.messageTimeTextView.setText(timeFormat.format(new Date(message.timestamp)));

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.messageBubble.getLayoutParams();

        if (message.senderId.equals(currentUserId)) {
            // Messaggio inviato: allineato a destra, sfondo blu chiaro
            params.gravity = Gravity.END;
            holder.messageBubble.setBackgroundColor(0xFFDCE8F5);
        } else {
            // Messaggio ricevuto: allineato a sinistra, sfondo grigio chiaro
            params.gravity = Gravity.START;
            holder.messageBubble.setBackgroundColor(0xFFF0F0F0);
        }
        holder.messageBubble.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(MessageEntity message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout messageBubble;
        TextView messageTextView;
        TextView messageTimeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            messageBubble = itemView.findViewById(R.id.messageBubble);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageTimeTextView = itemView.findViewById(R.id.messageTimeTextView);
        }
    }
}

