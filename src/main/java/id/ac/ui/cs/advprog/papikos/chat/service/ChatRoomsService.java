package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRooms;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomsService {

    private final ChatRoomsRepository chatRoomsRepository;

    @Autowired
    public ChatRoomsService(ChatRoomsRepository chatRoomsRepository) {
        this.chatRoomsRepository = chatRoomsRepository;
    }

    @Transactional
    public ChatRooms createChatRoom(Long user1Id, Long user2Id) {
        validateUserIds(user1Id, user2Id);
        Long[] orderedUserIds = orderUserIds(user1Id, user2Id);

        return chatRoomsRepository.findByUser1IdAndUser2Id(orderedUserIds[0], orderedUserIds[1])
                .orElseGet(() -> {
                    ChatRooms newRoom = new ChatRooms(orderedUserIds[0], orderedUserIds[1]);
                    return chatRoomsRepository.save(newRoom);
                });
    }

    @Transactional(readOnly = true)
    public ChatRooms getChatRoomById(Long roomId) {
        return chatRoomsRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
    }

    @Transactional(readOnly = true)
    public Optional<ChatRooms> findChatRoomByUserPair(Long user1Id, Long user2Id) {
        validateUserIds(user1Id, user2Id);
        Long[] orderedUserIds = orderUserIds(user1Id, user2Id);
        return chatRoomsRepository.findByUser1IdAndUser2Id(orderedUserIds[0], orderedUserIds[1]);
    }

    @Transactional(readOnly = true)
    public List<ChatRooms> getAllChatRoomsForUser(Long userId) {
        validateUserId(userId);
        return chatRoomsRepository.findByUser1IdOrUser2Id(userId, userId);
    }

    @Transactional
    public ChatRooms updateLastMessageAt(Long roomId, LocalDateTime timestamp) {
        ChatRooms room = getChatRoomById(roomId);
        room.setLastMessageAt(timestamp);
        return chatRoomsRepository.save(room);
    }

    private void validateUserIds(Long user1Id, Long user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("User IDs must be different");
        }
        validateUserId(user1Id);
        validateUserId(user2Id);
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId < 0) {
            throw new IllegalArgumentException("Invalid User ID");
        }
    }

    private Long[] orderUserIds(Long id1, Long id2) {
        return id1 < id2 ? new Long[]{id1, id2} : new Long[]{id2, id1};
    }
}