package com.example.bookey.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "messages",
        foreignKeys = {
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "userId",
                        childColumns = "senderId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = UserEntity.class,
                        parentColumns = "userId",
                        childColumns = "receiverId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"senderId"}),
                @Index(value = {"receiverId"}),
                @Index(value = {"senderId", "receiverId"})
        }
)
public class MessageEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String senderId;

    @NonNull
    public String receiverId;

    @NonNull
    public String messageText;

    public long timestamp;

    public boolean isRead;

    public MessageEntity(@NonNull String senderId, @NonNull String receiverId,
                          @NonNull String messageText, long timestamp, boolean isRead) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }
}

