package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomsServiceTest {

    @Mock
    private ChatRoomRepository chatRoomsRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    void createChatRoom_AutoOrderUsers() {
        UUID user1 = UUID.fromString("f47b3ac1-9ad1-4c87-bb9e-85f4c5fd7f57"); // lebih besar secara lexicographic
        UUID user2 = UUID.fromString("b219b3db-3bb1-4d9b-9436-937d78cdbb9c");
        ChatRoom expectedRoom = new ChatRoom(user1, user2);

        when(chatRoomsRepository.findByUser1IdAndUser2Id(user2, user1))
                .thenReturn(Optional.empty());
        when(chatRoomsRepository.save(any())).thenReturn(expectedRoom);

        ChatRoom result = chatRoomService.createChatRoom(user1, user2);

        assertEquals(user2, result.getUser1Id());
        assertEquals(user1, result.getUser2Id());
    }

    @Test
    void getAllChatRoomsForUser_ReturnsCombinedResults() {
        UUID userId = UUID.fromString("3d2f0c57-ccaf-4898-a4fc-2f4b350ceddf");
        List<ChatRoom> mockRooms = List.of(
                new ChatRoom(userId, UUID.randomUUID()),
                new ChatRoom(UUID.randomUUID(), userId)
        );

        when(chatRoomsRepository.findByUser1IdOrUser2Id(userId, userId))
                .thenReturn(mockRooms);

        List<ChatRoom> result = chatRoomService.getAllChatRoomsForUser(userId);

        assertEquals(2, result.size());
    }

    @Test
    void updateLastMessageAt_UpdatesCorrectly() {
        UUID roomId = UUID.randomUUID();
        LocalDateTime testTime = LocalDateTime.now();
        ChatRoom mockRoom = new ChatRoom(UUID.randomUUID(), UUID.randomUUID());

        when(chatRoomsRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));
        when(chatRoomsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChatRoom updated = chatRoomService.updateLastMessageAt(roomId, testTime);

        assertEquals(testTime, updated.getLastMessageAt());
    }
}