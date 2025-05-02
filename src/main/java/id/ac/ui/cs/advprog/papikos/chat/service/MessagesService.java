package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRooms;
import id.ac.ui.cs.advprog.papikos.chat.model.Messages;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomsRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessagesService {

    private final MessagesRepository messagesRepository;
    private final ChatRoomsRepository chatRoomsRepository;

    @Autowired
    public MessagesService(MessagesRepository messagesRepository,
                           ChatRoomsRepository chatRoomsRepository) {
        this.messagesRepository = messagesRepository;
        this.chatRoomsRepository = chatRoomsRepository;
    }

    public Messages sendMessage(Long roomId, Long senderUserId, String content) {
        Messages newMessage = new Messages(roomId, senderUserId, content);
        Messages savedMessage = messagesRepository.save(newMessage);

        ChatRooms chatRoom = chatRoomsRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        chatRoom.setLastMessageAt(savedMessage.getCreatedAt());
        chatRoomsRepository.save(chatRoom);

        return savedMessage;
    }

    public List<Messages> getMessagesByRoomAsc(Long roomId) {
        return messagesRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    public List<Messages> getMessagesByRoomDesc(Long roomId) {
        return messagesRepository.findByRoomIdOrderByCreatedAtDesc(roomId);
    }

    public Messages editMessageContent(Long messageId, Long userId, String newContent) {
        Optional<Messages> messageOpt = messagesRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }

        Messages message = messageOpt.get();
        if (!message.getSenderUserId().equals(userId)) {
            throw new SecurityException("Unauthorized to edit this message");
        }

        if (message.isDeleted()) {
            throw new IllegalStateException("Cannot edit deleted message");
        }

        message.setContent(newContent);
        message.setEdited(true);
        return messagesRepository.save(message);
    }

    public Messages markMessageAsDeleted(Long messageId, Long userId) {
        Optional<Messages> messageOpt = messagesRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }

        Messages message = messageOpt.get();
        if (!message.getSenderUserId().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this message");
        }

        message.setDeleted(true);
        message.setContent("[deleted]");
        return messagesRepository.save(message);
    }

    public Optional<Messages> getLatestMessageInRoom(Long roomId) {
        return messagesRepository.findFirstByRoomIdOrderByCreatedAtDesc(roomId);
    }
}