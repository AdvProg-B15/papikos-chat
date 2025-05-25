package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import id.ac.ui.cs.advprog.papikos.chat.repository.MessageRepository;
import id.ac.ui.cs.advprog.papikos.chat.sse.ChatSseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
class MessagesServiceTest {

    @Mock
    private MessageRepository messageRepository; // 'messagesRepository' di service

    @Mock
    private ChatRoomRepository chatRoomRepository; // 'chatRoomsRepository' di service

    @Mock
    private ChatSseService chatSseService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private UUID roomId;
    private UUID senderUserId;
    private UUID messageId;
    private String content;
    private ChatRoom chatRoomMock;

    @BeforeEach
    void setUp() {
        roomId = UUID.randomUUID();
        senderUserId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        content = "Hello, world!";

        chatRoomMock = mock(ChatRoom.class);
    }

    @Test
    void sendMessage_Success() {
        Message savedMessage = Message.builder()
                .messageId(UUID.randomUUID())
                .roomId(roomId)
                .senderUserId(senderUserId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .isEdited(false)
                .isDeleted(false)
                .build();

        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoomMock));
        when(chatRoomRepository.save(chatRoomMock)).thenReturn(chatRoomMock);
        doNothing().when(chatSseService).sendMessageToRoom(roomId);

        Message result = messageService.sendMessage(roomId, senderUserId, content);

        assertNotNull(result);
        assertEquals(savedMessage.getMessageId(), result.getMessageId());
        assertEquals(senderUserId, result.getSenderUserId());
        assertEquals(content, result.getContent());
        assertEquals(savedMessage.getCreatedAt(), result.getCreatedAt());
        assertFalse(result.isEdited());
        assertFalse(result.isDeleted());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());
        Message messageToSave = messageCaptor.getValue();
        assertEquals(roomId, messageToSave.getRoomId());
        assertEquals(senderUserId, messageToSave.getSenderUserId());
        assertEquals(content, messageToSave.getContent());
        assertFalse(messageToSave.isEdited(), "Newly created message should not be edited");
        assertFalse(messageToSave.isDeleted(), "Newly created message should not be deleted");


        verify(chatRoomRepository).findById(roomId);
        verify(chatRoomMock).setLastMessageAt(savedMessage.getCreatedAt());
        verify(chatRoomRepository).save(chatRoomMock);
        verify(chatSseService).sendMessageToRoom(roomId);
    }

    @Test
    void sendMessage_ChatRoomNotFound_ThrowsIllegalArgumentException() {

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        Message messageToBeSaved = new Message(roomId, senderUserId, content);
        when(messageRepository.save(any(Message.class))).thenReturn(messageToBeSaved);


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            messageService.sendMessage(roomId, senderUserId, content);
        });
        assertEquals("Chat room not found", exception.getMessage());

        verify(messageRepository).save(any(Message.class));
        verify(chatRoomRepository).findById(roomId);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
        verify(chatSseService, never()).sendMessageToRoom(any(UUID.class));
    }


    @Test
    void sendMessage_InvalidContent_ThrowsIllegalArgumentExceptionFromModel() {
        String invalidContent = " "; // Konten kosong atau null akan ditolak oleh constructor Message
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.sendMessage(roomId, senderUserId, invalidContent);
        }, "Message content cannot be empty"); // Pesan dari constructor Message

        verify(messageRepository, never()).save(any(Message.class));
        verify(chatRoomRepository, never()).findById(any(UUID.class));
    }


    @Test
    void getMessagesByRoomAsc_Success() {
        Message msg1 = Message.builder().roomId(roomId).senderUserId(senderUserId).content("msg1").createdAt(LocalDateTime.now().minusMinutes(2)).build();
        Message msg2 = Message.builder().roomId(roomId).senderUserId(senderUserId).content("msg2").createdAt(LocalDateTime.now().minusMinutes(1)).build();
        List<Message> expectedMessages = Arrays.asList(msg1, msg2);

        when(messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId)).thenReturn(expectedMessages);

        List<Message> actualMessages = messageService.getMessagesByRoomAsc(roomId);

        assertEquals(expectedMessages, actualMessages);
        verify(messageRepository).findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    @Test
    void getMessagesByRoomDesc_Success() {
        Message msg1 = Message.builder().roomId(roomId).senderUserId(senderUserId).content("msg1").createdAt(LocalDateTime.now().minusMinutes(1)).build(); // msg1 lebih baru
        Message msg2 = Message.builder().roomId(roomId).senderUserId(senderUserId).content("msg2").createdAt(LocalDateTime.now().minusMinutes(2)).build();
        List<Message> expectedMessages = Arrays.asList(msg1, msg2);

        when(messageRepository.findByRoomIdOrderByCreatedAtDesc(roomId)).thenReturn(expectedMessages);

        List<Message> actualMessages = messageService.getMessagesByRoomDesc(roomId);

        assertEquals(expectedMessages, actualMessages);
        verify(messageRepository).findByRoomIdOrderByCreatedAtDesc(roomId);
    }

    @Test
    void editMessageContent_Success() {
        String newContent = "Updated content";
        Message messageToEdit = Message.builder()
                .messageId(messageId)
                .roomId(roomId)
                .senderUserId(senderUserId)
                .content(content)
                .isDeleted(false)
                .isEdited(false)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(messageToEdit));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            return invocation.<Message>getArgument(0);
        });
        doNothing().when(chatSseService).sendMessageToRoom(roomId);

        Message result = messageService.editMessageContent(roomId, messageId, senderUserId, newContent);

        assertNotNull(result);
        assertEquals(messageId, result.getMessageId());
        assertEquals(newContent, result.getContent());
        assertTrue(result.isEdited());
        assertFalse(result.isDeleted());

        verify(messageRepository).findById(messageId);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());
        Message savedArg = messageCaptor.getValue();
        assertEquals(newContent, savedArg.getContent());
        assertTrue(savedArg.isEdited());

        verify(chatSseService).sendMessageToRoom(roomId);
    }

    @Test
    void editMessageContent_MessageNotFound_ThrowsIllegalArgumentException() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            messageService.editMessageContent(roomId, messageId, senderUserId, "new content");
        });
        assertEquals("Message not found", exception.getMessage());
        verify(messageRepository).findById(messageId);
        verify(messageRepository, never()).save(any(Message.class));
        verify(chatSseService, never()).sendMessageToRoom(any(UUID.class));
    }

    @Test
    void editMessageContent_UnauthorizedUser_ThrowsSecurityException() {
        UUID attackerId = UUID.randomUUID();
        Message messageToEdit = Message.builder()
                .messageId(messageId)
                .roomId(roomId)
                .senderUserId(senderUserId)
                .content(content)
                .isDeleted(false)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(messageToEdit));

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            messageService.editMessageContent(roomId, messageId, attackerId, "new content");
        });
        assertEquals("Unauthorized to edit this message", exception.getMessage());
        verify(messageRepository).findById(messageId);
        verify(messageRepository, never()).save(any(Message.class));
        verify(chatSseService, never()).sendMessageToRoom(any(UUID.class));
    }

    @Test
    void editMessageContent_MessageIsDeleted_ThrowsIllegalStateException() {
        Message deletedMessage = Message.builder()
                .messageId(messageId)
                .roomId(roomId)
                .senderUserId(senderUserId)
                .content(content)
                .isDeleted(true)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(deletedMessage));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            messageService.editMessageContent(roomId, messageId, senderUserId, "new content");
        });
        assertEquals("Cannot edit deleted message", exception.getMessage());
        verify(messageRepository).findById(messageId);
        verify(messageRepository, never()).save(any(Message.class));
        verify(chatSseService, never()).sendMessageToRoom(any(UUID.class));
    }

    @Test
    void markMessageAsDeleted_Success() {
        Message messageToDelete = Message.builder()
                .messageId(messageId)
                .roomId(roomId)
                .senderUserId(senderUserId)
                .content(content)
                .isDeleted(false)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(messageToDelete));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(chatSseService).sendMessageToRoom(roomId);

        Message result = messageService.markMessageAsDeleted(roomId, messageId, senderUserId);

        assertNotNull(result);
        assertEquals(messageId, result.getMessageId());
        assertTrue(result.isDeleted());
        assertEquals("[deleted]", result.getContent()); // Konten diubah oleh service

        verify(messageRepository).findById(messageId);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());
        assertTrue(messageCaptor.getValue().isDeleted());
        assertEquals("[deleted]", messageCaptor.getValue().getContent());

        verify(chatSseService).sendMessageToRoom(roomId);
    }

    @Test
    void markMessageAsDeleted_MessageNotFound_ThrowsIllegalArgumentException() {
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            messageService.markMessageAsDeleted(roomId, messageId, senderUserId);
        });
        assertEquals("Message not found", exception.getMessage());
        verify(messageRepository).findById(messageId); // findById masih dipanggil
        verify(messageRepository, never()).save(any(Message.class));
        verify(chatSseService, never()).sendMessageToRoom(any(UUID.class));
    }

    @Test
    void markMessageAsDeleted_UnauthorizedUser_ThrowsSecurityException() {
        UUID attackerId = UUID.randomUUID();
        Message messageToDelete = Message.builder()
                .messageId(messageId)
                .roomId(roomId)
                .senderUserId(senderUserId)
                .content(content)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(messageToDelete));

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            messageService.markMessageAsDeleted(roomId, messageId, attackerId);
        });
        assertEquals("Unauthorized to delete this message", exception.getMessage());
        verify(messageRepository).findById(messageId); // findById masih dipanggil
        verify(messageRepository, never()).save(any(Message.class));
        verify(chatSseService, never()).sendMessageToRoom(any(UUID.class));
    }

    @Test
    void getLatestMessageInRoom_MessageExists() {
        Message latestMessage = Message.builder().roomId(roomId).senderUserId(senderUserId).content("latest").build();
        when(messageRepository.findFirstByRoomIdOrderByCreatedAtDesc(roomId)).thenReturn(Optional.of(latestMessage));

        Optional<Message> result = messageService.getLatestMessageInRoom(roomId);

        assertTrue(result.isPresent());
        assertEquals(latestMessage, result.get());
        verify(messageRepository).findFirstByRoomIdOrderByCreatedAtDesc(roomId);
    }

    @Test
    void getLatestMessageInRoom_NoMessageExists() {
        when(messageRepository.findFirstByRoomIdOrderByCreatedAtDesc(roomId)).thenReturn(Optional.empty());

        Optional<Message> result = messageService.getLatestMessageInRoom(roomId);

        assertFalse(result.isPresent());
        verify(messageRepository).findFirstByRoomIdOrderByCreatedAtDesc(roomId);
    }
}