package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.MessageRepository;
import id.ac.ui.cs.advprog.papikos.chat.sse.ChatSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messagesRepository;
    private final ChatRoomRepository chatRoomsRepository;
    private final ChatSseService chatSseService;

    @Autowired
    public MessageServiceImpl(MessageRepository messagesRepository,
                              ChatRoomRepository chatRoomsRepository, @Lazy ChatSseService chatSseService) {
        this.messagesRepository = messagesRepository;
        this.chatRoomsRepository = chatRoomsRepository;
        this.chatSseService = chatSseService;

    }

    public Message sendMessage(UUID roomId, UUID senderUserId, String content) {
        Message newMessage = new Message(roomId, senderUserId, content);
        Message savedMessage = messagesRepository.save(newMessage);

        ChatRoom chatRoom = chatRoomsRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        chatRoom.setLastMessageAt(savedMessage.getCreatedAt());
        chatRoomsRepository.save(chatRoom);

        chatSseService.sendMessageToRoom(roomId);

        return savedMessage;
    }

    public List<Message> getMessagesByRoomAsc(UUID roomId) {
        return messagesRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    public List<Message> getMessagesByRoomDesc(UUID roomId) {
        return messagesRepository.findByRoomIdOrderByCreatedAtDesc(roomId);
    }

    public Message editMessageContent(UUID messageId, UUID userId, String newContent) {
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

    public Message markMessageAsDeleted(UUID messageId, UUID userId) {
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

    public Optional<Message> getLatestMessageInRoom(UUID roomId) {
        return messagesRepository.findFirstByRoomIdOrderByCreatedAtDesc(roomId);
    }
}