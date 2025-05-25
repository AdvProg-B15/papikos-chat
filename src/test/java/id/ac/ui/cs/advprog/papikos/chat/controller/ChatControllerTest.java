package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import id.ac.ui.cs.advprog.papikos.chat.sse.ChatSseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private MessageService messageService;

    @Mock
    private ChatSseService chatSseService;

    @InjectMocks
    private ChatController chatController;

    private UUID userId1;
    private UUID userId2;
    private Authentication authUser1;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        authUser1 = new UsernamePasswordAuthenticationToken(userId1.toString(), null, Collections.emptyList());
    }

    @Test
    void getChatRoomsForUser_Success_ReturnsOk() {
        ChatRoom mockRoom = Mockito.mock(ChatRoom.class);

        List<ChatRoom> expectedRooms = Collections.singletonList(mockRoom);
        when(chatRoomService.getAllChatRoomsForUser(userId1)).thenReturn(expectedRooms);

        ResponseEntity<ApiResponse<List<ChatRoom>>> responseEntity = chatController.getChatRoomsForUser(authUser1);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse<List<ChatRoom>> apiResponse = responseEntity.getBody();
        assertNotNull(apiResponse);
        assertEquals(200, apiResponse.getStatus());
        assertEquals("Chat rooms retrieved successfully", apiResponse.getMessage());
        assertSame(expectedRooms, apiResponse.getData());

        verify(chatRoomService).getAllChatRoomsForUser(userId1);
    }

    @Test
    void getChatRoomsForUser_NullAuthentication_ThrowsIllegalStateException() {
        Exception exception = assertThrows(IllegalStateException.class, () -> chatController.getChatRoomsForUser(null));
        assertEquals("Authentication principal is required but missing.", exception.getMessage());
        verify(chatRoomService, never()).getAllChatRoomsForUser(any());
    }

    @Test
    void getChatRoomsForUser_InvalidAuthPrincipalFormat_ThrowsIllegalArgumentException() {
        Authentication invalidAuth = new UsernamePasswordAuthenticationToken("invalid-uuid-format", null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> chatController.getChatRoomsForUser(invalidAuth));
        assertEquals("Invalid user identifier format in authentication token.", exception.getMessage());
        verify(chatRoomService, never()).getAllChatRoomsForUser(any());
    }

    @Test
    void createChatRoom_Success_ReturnsCreated() {
        ChatRoomRequest request = new ChatRoomRequest();
        request.setRecipientId(userId2);

        ChatRoom mockCreatedRoom = Mockito.mock(ChatRoom.class);


        when(chatRoomService.createChatRoom(userId1, userId2)).thenReturn(mockCreatedRoom);

        ResponseEntity<ApiResponse<ChatRoom>> responseEntity = chatController.createChatRoom(request, authUser1);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ApiResponse<ChatRoom> apiResponse = responseEntity.getBody();
        assertNotNull(apiResponse);
        assertEquals(201, apiResponse.getStatus());
        assertEquals("Chat room created successfully!", apiResponse.getMessage());
        assertSame(mockCreatedRoom, apiResponse.getData());

        verify(chatRoomService).createChatRoom(userId1, userId2);
    }

    @Test
    void sendMessage_Success_ReturnsOkWithCreatedInBody() {
        UUID roomId = UUID.randomUUID();
        MessageRequest request = new MessageRequest();
        String content = "Test message content";
        request.setContent(content);

        Message mockSentMessage = Mockito.mock(Message.class);


        when(messageService.sendMessage(roomId, userId1, content)).thenReturn(mockSentMessage);

        ResponseEntity<ApiResponse<Message>> responseEntity = chatController.sendMessage(roomId, request, authUser1);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse<Message> apiResponse = responseEntity.getBody();
        assertNotNull(apiResponse);
        assertEquals(201, apiResponse.getStatus());
        assertEquals("Message sent successfully", apiResponse.getMessage());
        assertSame(mockSentMessage, apiResponse.getData());
        verify(messageService).sendMessage(roomId, userId1, content);
    }

    @Test
    void streamMessages_Success_ReturnsSseEmitter() {
        UUID roomId = UUID.randomUUID();
        SseEmitter mockEmitter = new SseEmitter();
        when(chatSseService.subscribeToRoom(roomId)).thenReturn(mockEmitter);

        SseEmitter resultEmitter = chatController.streamMessages(roomId);

        assertNotNull(resultEmitter);
        assertSame(mockEmitter, resultEmitter);
        verify(chatSseService).subscribeToRoom(roomId);
    }

    @Test
    void editMessage_Success_ReturnsOk() {
        UUID roomId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        MessageRequest request = new MessageRequest();
        String updatedContent = "Updated message content";
        request.setContent(updatedContent);

        Message mockUpdatedMessage = Mockito.mock(Message.class);


        when(messageService.editMessageContent(roomId, messageId, userId1, updatedContent)).thenReturn(mockUpdatedMessage);

        ResponseEntity<ApiResponse<Message>> responseEntity = chatController.editMessage(roomId, messageId, request, authUser1);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse<Message> apiResponse = responseEntity.getBody();
        assertNotNull(apiResponse);
        assertEquals(200, apiResponse.getStatus());
        assertEquals("Message updated successfully", apiResponse.getMessage());
        assertSame(mockUpdatedMessage, apiResponse.getData());
        verify(messageService).editMessageContent(roomId, messageId, userId1, updatedContent);
    }

    @Test
    void deleteMessage_Success_ReturnsOk() {
        UUID roomId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        Message mockDeletedMessage = Mockito.mock(Message.class);

        when(messageService.markMessageAsDeleted(roomId, messageId, userId1)).thenReturn(mockDeletedMessage);

        ResponseEntity<ApiResponse<Message>> responseEntity = chatController.deleteMessage(roomId, messageId, authUser1);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ApiResponse<Message> apiResponse = responseEntity.getBody();
        assertNotNull(apiResponse);
        assertEquals(200, apiResponse.getStatus());
        assertEquals("Message deleted successfully", apiResponse.getMessage());
        assertSame(mockDeletedMessage, apiResponse.getData());
        verify(messageService).markMessageAsDeleted(roomId, messageId, userId1);
    }
}