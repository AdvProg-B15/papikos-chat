package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRooms;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomsRepository;
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
    private ChatRoomsRepository chatRoomsRepository;

    @InjectMocks
    private ChatRoomsService chatRoomService;

    @Test
    void createChatRoom_AutoOrderUsers() {
        Long user1 = 2L;
        Long user2 = 1L;
        ChatRooms expectedRoom = new ChatRooms(1L, 2L);

        when(chatRoomsRepository.findByUser1IdAndUser2Id(1L, 2L))
                .thenReturn(Optional.empty());
        when(chatRoomsRepository.save(any())).thenReturn(expectedRoom);

        ChatRooms result = chatRoomService.createChatRoom(user1, user2);

        assertEquals(1L, result.getUser1Id());
        assertEquals(2L, result.getUser2Id());
    }

    @Test
    void getAllChatRoomsForUser_ReturnsCombinedResults() {
        Long userId = 3L;
        List<ChatRooms> mockRooms = List.of(
                new ChatRooms(3L, 1L),
                new ChatRooms(2L, 3L)
        );

        when(chatRoomsRepository.findByUser1IdOrUser2Id(userId, userId))
                .thenReturn(mockRooms);

        List<ChatRooms> result = chatRoomService.getAllChatRoomsForUser(userId);

        assertEquals(2, result.size());
    }

    @Test
    void updateLastMessageAt_UpdatesCorrectly() {
        Long roomId = 1L;
        LocalDateTime testTime = LocalDateTime.now();
        ChatRooms mockRoom = new ChatRooms(1L, 2L);

        when(chatRoomsRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));
        when(chatRoomsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChatRooms updated = chatRoomService.updateLastMessageAt(roomId, testTime);

        assertEquals(testTime, updated.getLastMessageAt());
    }
}