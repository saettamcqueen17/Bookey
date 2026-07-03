package com.example.bookey.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.bookey.Entity.FriendRequestEntity;
import com.example.bookey.Entity.MessageEntity;

import java.util.List;

@Dao
public interface FriendDao {

    // Richieste di amicizia

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long sendFriendRequest(FriendRequestEntity request);

    @Query("SELECT * FROM friend_requests WHERE toUserId = :userId AND status = 'PENDING' ORDER BY timestamp DESC")
    List<FriendRequestEntity> getPendingRequestsForUser(String userId);

    @Query("SELECT * FROM friend_requests WHERE " +
            "((fromUserId = :userId1 AND toUserId = :userId2) OR " +
            "(fromUserId = :userId2 AND toUserId = :userId1)) AND status = 'ACCEPTED' LIMIT 1")
    FriendRequestEntity getAcceptedFriendship(String userId1, String userId2);

    @Query("SELECT * FROM friend_requests WHERE " +
            "(fromUserId = :userId1 AND toUserId = :userId2) OR " +
            "(fromUserId = :userId2 AND toUserId = :userId1) LIMIT 1")
    FriendRequestEntity getExistingRequest(String userId1, String userId2);

    @Query("UPDATE friend_requests SET status = :status WHERE id = :requestId")
    void updateRequestStatus(long requestId, String status);

    @Query("SELECT COUNT(*) FROM friend_requests WHERE toUserId = :userId AND status = 'PENDING'")
    int getPendingRequestCount(String userId);


    @Query("SELECT * FROM friend_requests WHERE " +
            "(fromUserId = :userId OR toUserId = :userId) AND status = 'ACCEPTED' " +
            "ORDER BY timestamp DESC")
    List<FriendRequestEntity> getAcceptedFriends(String userId);



    @Insert
    long sendMessage(MessageEntity message);


    @Query("SELECT * FROM messages WHERE " +
            "(senderId = :userId1 AND receiverId = :userId2) OR " +
            "(senderId = :userId2 AND receiverId = :userId1) " +
            "ORDER BY timestamp ASC")
    List<MessageEntity> getConversation(String userId1, String userId2);


    @Query("UPDATE messages SET isRead = 1 WHERE receiverId = :currentUserId AND senderId = :otherUserId AND isRead = 0")
    void markMessagesAsRead(String currentUserId, String otherUserId);

    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND isRead = 0")
    int getUnreadMessageCount(String userId);
}

