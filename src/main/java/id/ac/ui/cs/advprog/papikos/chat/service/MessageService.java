package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messagesRepository;
    private final ChatRoomRepository chatRoomsRepository;

    @Autowired
    public MessageService(MessageRepository messagesRepository,
                          ChatRoomRepository chatRoomsRepository) {
        this.messagesRepository = messagesRepository;
        this.chatRoomsRepository = chatRoomsRepository;
    }

    public Message sendMessage(Long roomId, Long senderUserId, String content) {
        Message newMessage = new Message(roomId, senderUserId, content);
        Message savedMessage = messagesRepository.save(newMessage);

        ChatRoom chatRoom = chatRoomsRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        chatRoom.setLastMessageAt(savedMessage.getCreatedAt());
        chatRoomsRepository.save(chatRoom);

        return savedMessage;
    }

    public List<Message> getMessagesByRoomAsc(Long roomId) {
        return messagesRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    public List<Message> getMessagesByRoomDesc(Long roomId) {
        return messagesRepository.findByRoomIdOrderByCreatedAtDesc(roomId);
    }

    public Message editMessageContent(Long messageId, Long userId, String newContent) {
        Optional<Message> messageOpt = messagesRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }

        Message message = messageOpt.get();
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

    public Message markMessageAsDeleted(Long messageId, Long userId) {
        Optional<Message> messageOpt = messagesRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }

        Message message = messageOpt.get();
        if (!message.getSenderUserId().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this message");
        }

        message.setDeleted(true);
        message.setContent("[deleted]");
        return messagesRepository.save(message);
    }

    public Optional<Message> getLatestMessageInRoom(Long roomId) {
        return messagesRepository.findFirstByRoomIdOrderByCreatedAtDesc(roomId);
    }
}