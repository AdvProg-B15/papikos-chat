package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomsRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomsRepository) {
        this.chatRoomsRepository = chatRoomsRepository;
    }

    @Transactional
    public ChatRoom createChatRoom(Long user1Id, Long user2Id) {
        validateUserIds(user1Id, user2Id);
        Long[] orderedUserIds = orderUserIds(user1Id, user2Id);

        return chatRoomsRepository.findByUser1IdAndUser2Id(orderedUserIds[0], orderedUserIds[1])
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom(orderedUserIds[0], orderedUserIds[1]);
                    return chatRoomsRepository.save(newRoom);
                });
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(Long roomId) {
        return chatRoomsRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
    }

    @Transactional(readOnly = true)
    public Optional<ChatRoom> findChatRoomByUserPair(Long user1Id, Long user2Id) {
        validateUserIds(user1Id, user2Id);
        Long[] orderedUserIds = orderUserIds(user1Id, user2Id);
        return chatRoomsRepository.findByUser1IdAndUser2Id(orderedUserIds[0], orderedUserIds[1]);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getAllChatRoomsForUser(Long userId) {
        validateUserId(userId);
        return chatRoomsRepository.findByUser1IdOrUser2Id(userId, userId);
    }

    @Transactional
    public ChatRoom updateLastMessageAt(Long roomId, LocalDateTime timestamp) {
        ChatRoom room = getChatRoomById(roomId);
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