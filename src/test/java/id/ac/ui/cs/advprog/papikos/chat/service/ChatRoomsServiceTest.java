package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceImplTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    private UUID user1Id;
    private UUID user2Id;
    private UUID roomId;
    private ChatRoom mockChatRoom;

    @BeforeEach
    void setUp() {
        user1Id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        user2Id = UUID.fromString("00000000-0000-0000-0000-000000000002");
        roomId = UUID.randomUUID();
        mockChatRoom = Mockito.mock(ChatRoom.class);
    }

    @Test
    void createChatRoom_whenRoomDoesNotExist_shouldCreateAndSaveNewRoom() {
        when(chatRoomRepository.findByUser1IdAndUser2Id(user1Id, user2Id)).thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> {
            ChatRoom roomToSave = invocation.getArgument(0);
            return roomToSave;
        });

        ChatRoom createdRoom = chatRoomService.createChatRoom(user1Id, user2Id);

        assertNotNull(createdRoom);
        assertEquals(user1Id, createdRoom.getUser1Id());
        assertEquals(user2Id, createdRoom.getUser2Id());

        ArgumentCaptor<ChatRoom> chatRoomCaptor = ArgumentCaptor.forClass(ChatRoom.class);
        verify(chatRoomRepository).save(chatRoomCaptor.capture());
        ChatRoom savedRoom = chatRoomCaptor.getValue();
        assertEquals(user1Id, savedRoom.getUser1Id());
        assertEquals(user2Id, savedRoom.getUser2Id());

        verify(chatRoomRepository).findByUser1IdAndUser2Id(user1Id, user2Id);
    }

    @Test
    void createChatRoom_whenRoomExists_shouldReturnExistingRoom() {
        when(chatRoomRepository.findByUser1IdAndUser2Id(user1Id, user2Id)).thenReturn(Optional.of(mockChatRoom));

        ChatRoom existingRoom = chatRoomService.createChatRoom(user1Id, user2Id);

        assertNotNull(existingRoom);
        assertSame(mockChatRoom, existingRoom);
        verify(chatRoomRepository).findByUser1IdAndUser2Id(user1Id, user2Id);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    void createChatRoom_withUnorderedUserIds_shouldOrderAndCreateOrFind() {
        when(chatRoomRepository.findByUser1IdAndUser2Id(user1Id, user2Id)).thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChatRoom createdRoom = chatRoomService.createChatRoom(user2Id, user1Id);

        assertNotNull(createdRoom);
        assertEquals(user1Id, createdRoom.getUser1Id());
        assertEquals(user2Id, createdRoom.getUser2Id());

        verify(chatRoomRepository).findByUser1IdAndUser2Id(user1Id, user2Id);
        ArgumentCaptor<ChatRoom> captor = ArgumentCaptor.forClass(ChatRoom.class);
        verify(chatRoomRepository).save(captor.capture());
        assertEquals(user1Id, captor.getValue().getUser1Id());
        assertEquals(user2Id, captor.getValue().getUser2Id());
    }

    @Test
    void createChatRoom_withNullUser1Id_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatRoomService.createChatRoom(null, user2Id);
        });
        assertEquals("User IDs cannot be null", exception.getMessage());
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void createChatRoom_withNullUser2Id_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatRoomService.createChatRoom(user1Id, null);
        });
        assertEquals("User IDs cannot be null", exception.getMessage());
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void createChatRoom_withSameUserIds_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatRoomService.createChatRoom(user1Id, user1Id);
        });
        assertEquals("User IDs must be different", exception.getMessage());
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void getChatRoomById_whenRoomExists_shouldReturnRoom() {
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(mockChatRoom));
        ChatRoom foundRoom = chatRoomService.getChatRoomById(roomId);
        assertSame(mockChatRoom, foundRoom);
        verify(chatRoomRepository).findById(roomId);
    }

    @Test
    void getChatRoomById_whenRoomDoesNotExist_shouldThrowIllegalArgumentException() {
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatRoomService.getChatRoomById(roomId);
        });
        assertEquals("Chat room not found", exception.getMessage());
        verify(chatRoomRepository).findById(roomId);
    }

    @Test
    void getAllChatRoomsForUser_whenUserHasRooms_shouldReturnListOfRooms() {
        ChatRoom anotherMockChatRoom = Mockito.mock(ChatRoom.class);
        List<ChatRoom> expectedRooms = Arrays.asList(mockChatRoom, anotherMockChatRoom);
        when(chatRoomRepository.findByUser1IdOrUser2Id(user1Id, user1Id)).thenReturn(expectedRooms);

        List<ChatRoom> actualRooms = chatRoomService.getAllChatRoomsForUser(user1Id);

        assertEquals(expectedRooms, actualRooms);
        verify(chatRoomRepository).findByUser1IdOrUser2Id(user1Id, user1Id);
    }

    @Test
    void getAllChatRoomsForUser_whenUserHasNoRooms_shouldReturnEmptyList() {
        when(chatRoomRepository.findByUser1IdOrUser2Id(user1Id, user1Id)).thenReturn(List.of());
        List<ChatRoom> actualRooms = chatRoomService.getAllChatRoomsForUser(user1Id);
        assertTrue(actualRooms.isEmpty());
        verify(chatRoomRepository).findByUser1IdOrUser2Id(user1Id, user1Id);
    }

    @Test
    void getAllChatRoomsForUser_withNullUserId_shouldThrowIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatRoomService.getAllChatRoomsForUser(null);
        });
        assertEquals("Invalid User ID", exception.getMessage());
        verifyNoInteractions(chatRoomRepository);
    }

    @Test
    void updateLastMessageAt_whenRoomExists_shouldUpdateAndSaveRoom() {
        LocalDateTime newTimestamp = LocalDateTime.now();
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(mockChatRoom));
        when(chatRoomRepository.save(mockChatRoom)).thenReturn(mockChatRoom);

        ChatRoom updatedRoom = chatRoomService.updateLastMessageAt(roomId, newTimestamp);

        assertSame(mockChatRoom, updatedRoom);
        verify(chatRoomRepository).findById(roomId);
        verify(mockChatRoom).setLastMessageAt(newTimestamp);
        verify(chatRoomRepository).save(mockChatRoom);
    }

    @Test
    void updateLastMessageAt_whenRoomDoesNotExist_shouldThrowIllegalArgumentException() {
        LocalDateTime newTimestamp = LocalDateTime.now();
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chatRoomService.updateLastMessageAt(roomId, newTimestamp);
        });
        assertEquals("Chat room not found", exception.getMessage());
        verify(chatRoomRepository).findById(roomId);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }
}