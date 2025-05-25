package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message sendMessage(UUID roomId, UUID senderUserId, String content);

    List<Message> getMessagesByRoomAsc(UUID roomId);

    List<Message> getMessagesByRoomDesc(UUID roomId);

    Message editMessageContent(UUID roomId, UUID messageId, UUID userId, String newContent);

    Message markMessageAsDeleted(UUID roomId, UUID messageId, UUID userId);

    Optional<Message> getLatestMessageInRoom(UUID roomId);
}