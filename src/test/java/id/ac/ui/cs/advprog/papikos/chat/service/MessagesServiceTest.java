package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.MessageRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagesServiceTest {

    @Mock
    private MessageRepository messagesRepository;

    @Mock
    private ChatRoomRepository chatRoomsRepository;

    @InjectMocks
    private MessageService messagesService;

    private Message testMessage;
    private final UUID validRoomId = UUID.randomUUID();
    private final UUID validUserId = UUID.randomUUID();
    private final UUID invalidUserId = UUID.randomUUID();
    private final UUID messageId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testMessage = new Message(validRoomId, validUserId, "Test message");
        testMessage.setMessageId(messageId);
        testMessage.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSendMessage() {
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        String messageContent = "Hello World";

        ChatRoom mockRoom = new ChatRoom(user1Id, user2Id);
        mockRoom.setRoomId(validRoomId);

        LocalDateTime testTime = LocalDateTime.now();

        when(messagesRepository.save(any()))
                .thenAnswer(invocation -> {
                    Message msg = invocation.getArgument(0);
                    msg.setMessageId(UUID.randomUUID());
                    msg.setCreatedAt(testTime);
                    msg.setUpdatedAt(testTime);
                    return msg;
                });

        when(chatRoomsRepository.findById(validRoomId))
                .thenReturn(Optional.of(mockRoom));

        Message result = messagesService.sendMessage(validRoomId, validUserId, messageContent);

        assertNotNull(result, "Result should not be null");
        assertEquals(validUserId, result.getSenderUserId(), "Sender ID mismatch");
        assertEquals(validRoomId, result.getRoomId(), "Room ID mismatch");
        assertEquals(messageContent, result.getContent(), "Content mismatch");
        assertEquals(testTime, result.getCreatedAt(), "Created timestamp mismatch");
        assertEquals(testTime, result.getUpdatedAt(), "Updated timestamp mismatch");

        verify(messagesRepository, times(1)).save(any());
        verify(chatRoomsRepository, times(1)).findById(validRoomId);
        verify(chatRoomsRepository, times(1)).save(any());

        ChatRoom updatedRoom = chatRoomsRepository.findById(validRoomId).orElseThrow();
        assertEquals(testTime, updatedRoom.getLastMessageAt(), "Last message timestamp not updated");
    }

    @Test
    void testGetMessagesByRoomAsc() {
        when(messagesRepository.findByRoomIdOrderByCreatedAtAsc(validRoomId))
                .thenReturn(Collections.singletonList(testMessage));

        List<Message> messages = messagesService.getMessagesByRoomAsc(validRoomId);

        assertEquals(1, messages.size());
        assertEquals(testMessage, messages.get(0));
    }

    @Test
    void testEditMessageContent_Success() {
        when(messagesRepository.findById(messageId)).thenReturn(Optional.of(testMessage));
        when(messagesRepository.save(any(Message.class))).thenReturn(testMessage);

        Message editedMessage = messagesService.editMessageContent(messageId, validUserId, "Edited message");

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
        when(messagesRepository.save(any(Message.class))).thenReturn(testMessage);

        Message deletedMessage = messagesService.markMessageAsDeleted(messageId, validUserId);

        assertTrue(deletedMessage.isDeleted());
        assertEquals("[deleted]", deletedMessage.getContent());
        verify(messagesRepository, times(1)).save(testMessage);
    }

    @Test
    void testGetLatestMessageInRoom() {
        when(messagesRepository.findFirstByRoomIdOrderByCreatedAtDesc(validRoomId))
                .thenReturn(Optional.of(testMessage));

        Optional<Message> result = messagesService.getLatestMessageInRoom(validRoomId);
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
        when(messagesRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                messagesService.editMessageContent(UUID.randomUUID(), validUserId, "Edited message"));
    }

    @Test
    void testEmptyRoomMessages() {
        UUID emptyRoomId = UUID.randomUUID();
        when(messagesRepository.findByRoomIdOrderByCreatedAtAsc(emptyRoomId))
                .thenReturn(Collections.emptyList());

        List<Message> messages = messagesService.getMessagesByRoomAsc(emptyRoomId);

        assertTrue(messages.isEmpty());
    }
}
