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
        Long user1 = 2L;
        Long user2 = 1L;
        ChatRoom expectedRoom = new ChatRoom(1L, 2L);

        when(chatRoomsRepository.findByUser1IdAndUser2Id(1L, 2L))
                .thenReturn(Optional.empty());
        when(chatRoomsRepository.save(any())).thenReturn(expectedRoom);

        ChatRoom result = chatRoomService.createChatRoom(user1, user2);

        assertEquals(1L, result.getUser1Id());
        assertEquals(2L, result.getUser2Id());
    }

    @Test
    void getAllChatRoomsForUser_ReturnsCombinedResults() {
        Long userId = 3L;
        List<ChatRoom> mockRooms = List.of(
                new ChatRoom(3L, 1L),
                new ChatRoom(2L, 3L)
        );

        when(chatRoomsRepository.findByUser1IdOrUser2Id(userId, userId))
                .thenReturn(mockRooms);

        List<ChatRoom> result = chatRoomService.getAllChatRoomsForUser(userId);

        assertEquals(2, result.size());
    }

    @Test
    void updateLastMessageAt_UpdatesCorrectly() {
        Long roomId = 1L;
        LocalDateTime testTime = LocalDateTime.now();
        ChatRoom mockRoom = new ChatRoom(1L, 2L);

        when(chatRoomsRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));
        when(chatRoomsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChatRoom updated = chatRoomService.updateLastMessageAt(roomId, testTime);

        assertEquals(testTime, updated.getLastMessageAt());
    }
}