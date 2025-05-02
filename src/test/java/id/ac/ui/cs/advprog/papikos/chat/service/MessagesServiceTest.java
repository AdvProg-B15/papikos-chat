package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRooms;
import id.ac.ui.cs.advprog.papikos.chat.model.Messages;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomsRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.MessagesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagesServiceTest {

    @Mock
    private MessagesRepository messagesRepository;

    @Mock
    private ChatRoomsRepository chatRoomsRepository;

    @InjectMocks
    private MessagesService messagesService;

    private Messages testMessage;
    private final Long validRoomId = 1L;
    private final Long validUserId = 1L;
    private final Long invalidUserId = 2L;
    private final Long messageId = 1L;

    @BeforeEach
    void setUp() {
        testMessage = new Messages(validRoomId, validUserId, "Test message");
        testMessage.setMessageId(messageId);
        testMessage.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSendMessage() {
        Long user1Id = 1L;
        Long user2Id = 2L;
        String messageContent = "Hello World";

        ChatRooms mockRoom = new ChatRooms(user1Id, user2Id);
        mockRoom.setRoomId(validRoomId);

        LocalDateTime testTime = LocalDateTime.now();

        when(messagesRepository.save(any()))
                .thenAnswer(invocation -> {
                    Messages msg = invocation.getArgument(0);
                    msg.setMessageId(1L);
                    msg.setCreatedAt(testTime);
                    msg.setUpdatedAt(testTime);
                    return msg;
                });

        when(chatRoomsRepository.findById(validRoomId))
                .thenReturn(Optional.of(mockRoom));

        Messages result = messagesService.sendMessage(validRoomId, validUserId, messageContent);


        assertNotNull(result, "Result should not be null");
        assertEquals(validUserId, result.getSenderUserId(), "Sender ID mismatch");
        assertEquals(validRoomId, result.getRoomId(), "Room ID mismatch");
        assertEquals(messageContent, result.getContent(), "Content mismatch");
        assertEquals(testTime, result.getCreatedAt(), "Created timestamp mismatch");
        assertEquals(testTime, result.getUpdatedAt(), "Updated timestamp mismatch");

        verify(messagesRepository, times(1)).save(any());
        verify(chatRoomsRepository, times(1)).findById(validRoomId);
        verify(chatRoomsRepository, times(1)).save(any());

        ChatRooms updatedRoom = chatRoomsRepository.findById(validRoomId).orElseThrow();
        assertEquals(testTime, updatedRoom.getLastMessageAt(), "Last message timestamp not updated");
    }

    @Test
    void testGetMessagesByRoomAsc() {
        when(messagesRepository.findByRoomIdOrderByCreatedAtAsc(validRoomId))
                .thenReturn(Collections.singletonList(testMessage));

        List<Messages> messages = messagesService.getMessagesByRoomAsc(validRoomId);

        assertEquals(1, messages.size());
        assertEquals(testMessage, messages.getFirst());
    }

    @Test
    void testEditMessageContent_Success() {
        when(messagesRepository.findById(messageId)).thenReturn(Optional.of(testMessage));
        when(messagesRepository.save(any(Messages.class))).thenReturn(testMessage);

        Messages editedMessage = messagesService.editMessageContent(messageId, validUserId, "Edited message");

        assertEquals("Edited message", editedMessage.getContent());
        assertTrue(editedMessage.isEdited());
        verify(messagesRepository, times(1)).save(testMessage);
    }

    @Test
    void testEditMessageContent_UnauthorizedUser() {
        when(messagesRepository.findById(messageId)).thenReturn(Optional.of(testMessage));

        assertThrows(SecurityException.class, () ->
                messagesService.editMessageContent(messageId, invalidUserId, "Edited message"));
    }

    @Test
    void testMarkMessageAsDeleted_Success() {
        when(messagesRepository.findById(messageId)).thenReturn(Optional.of(testMessage));
        when(messagesRepository.save(any(Messages.class))).thenReturn(testMessage);

        Messages deletedMessage = messagesService.markMessageAsDeleted(messageId, validUserId);

        assertTrue(deletedMessage.isDeleted());
        assertEquals("[deleted]", deletedMessage.getContent());
        verify(messagesRepository, times(1)).save(testMessage);
    }

    @Test
    void testGetLatestMessageInRoom() {
        when(messagesRepository.findFirstByRoomIdOrderByCreatedAtDesc(validRoomId))
                .thenReturn(Optional.of(testMessage));

        Optional<Messages> result = messagesService.getLatestMessageInRoom(validRoomId);
        assertTrue(result.isPresent());
    }

    @Test
    void testEditDeletedMessage_ShouldThrow() {
        testMessage.setDeleted(true);
        when(messagesRepository.findById(messageId)).thenReturn(Optional.of(testMessage));

        assertThrows(IllegalStateException.class, () ->
                messagesService.editMessageContent(messageId, validUserId, "Edited message"));
    }

    @Test
    void testMessageNotFound_ShouldThrow() {
        when(messagesRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                messagesService.editMessageContent(999L, validUserId, "Edited message"));
    }

    @Test
    void testEmptyRoomMessages() {
        Long emptyRoomId = 2L;
        when(messagesRepository.findByRoomIdOrderByCreatedAtAsc(emptyRoomId))
                .thenReturn(Collections.emptyList());

        List<Messages> messages = messagesService.getMessagesByRoomAsc(emptyRoomId);

        assertTrue(messages.isEmpty());
    }
}