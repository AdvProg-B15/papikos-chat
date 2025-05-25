package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import id.ac.ui.cs.advprog.papikos.chat.sse.ChatSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final ChatSseService chatSseService;

    @Autowired
    public ChatController(ChatRoomService chatRoomService, MessageService messageService, ChatSseService chatSseService) {
        this.chatRoomService = chatRoomService;
        this.messageService = messageService;
        this.chatSseService = chatSseService;
    }

    /**
     * Helper method to extract UUID from the authentication principal.
     * Throws an IllegalStateException if the authentication or principal is missing, or if the principal is not a valid UUID.
     */
    private UUID getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("Authentication principal is required but missing.");
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing UUID from principal name: " + authentication.getName());
            throw new IllegalArgumentException("Invalid user identifier format in authentication token.");
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatRoom>>> getChatRoomsForUser(Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        List<ChatRoom> chatRooms = chatRoomService.getAllChatRoomsForUser(userId);
        ApiResponse<List<ChatRoom>> response = ApiResponse.<List<ChatRoom>>builder()
                .status(HttpStatus.OK)
                .message("Chat rooms retrieved successfully")
                .data(chatRooms)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoom>> createChatRoom(@RequestBody ChatRoomRequest request, Authentication authentication) {
        UUID senderId = getUserIdFromAuthentication(authentication);
        ChatRoom chatRoom = chatRoomService.createChatRoom(senderId, request.getRecipientId());
        ApiResponse<ChatRoom> response = ApiResponse.<ChatRoom>builder()
                .status(HttpStatus.CREATED)
                .message("Chat room created successfully!")
                .data(chatRoom)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<Message>> sendMessage(@PathVariable UUID roomId,
                                                            @RequestBody MessageRequest request,
                                                            Authentication authentication) {
        UUID senderId = getUserIdFromAuthentication(authentication);
        Message message = messageService.sendMessage(roomId, senderId, request.getContent());
        ApiResponse<Message> response = ApiResponse.<Message>builder()
                .status(HttpStatus.CREATED)
                .message("Message sent successfully")
                .data(message)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{roomId}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessages(@PathVariable UUID roomId) {
        return chatSseService.subscribeToRoom(roomId);
    }

    @PutMapping("/{roomId}/message/{messageId}")
    public ResponseEntity<ApiResponse<Message>> editMessage(@PathVariable UUID roomId,
                                                            @PathVariable UUID messageId,
                                                            @RequestBody MessageRequest request,
                                                            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        Message updatedMessage = messageService.editMessageContent(messageId, userId, request.getContent());
        ApiResponse<Message> response = ApiResponse.<Message>builder()
                .status(HttpStatus.OK)
                .message("Message updated successfully")
                .data(updatedMessage)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{roomId}/message/{messageId}")
    public ResponseEntity<ApiResponse<Message>> deleteMessage(@PathVariable UUID roomId,
                                                              @PathVariable UUID messageId,
                                                              Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        Message deletedMessage = messageService.markMessageAsDeleted(messageId, userId);
        ApiResponse<Message> response = ApiResponse.<Message>builder()
                .status(HttpStatus.OK)
                .message("Message deleted successfully")
                .data(deletedMessage)
                .build();
        return ResponseEntity.ok(response);
    }
}