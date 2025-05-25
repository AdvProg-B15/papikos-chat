package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.command.CreateChatRoomCommand;
import id.ac.ui.cs.advprog.papikos.chat.command.DeleteMessageCommand;
import id.ac.ui.cs.advprog.papikos.chat.command.EditMessageCommand;
import id.ac.ui.cs.advprog.papikos.chat.command.GetChatRoomsCommand;
import id.ac.ui.cs.advprog.papikos.chat.command.SendMessageCommand;
import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import id.ac.ui.cs.advprog.papikos.chat.sse.ChatSseService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatRoom>>> getChatRoomsForUser(Authentication authentication) {
        return new GetChatRoomsCommand(authentication, chatRoomService).execute();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoom>> createChatRoom(@RequestBody ChatRoomRequest request) {
        return new CreateChatRoomCommand(chatRoomService, request).execute();
    }

    @PostMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<Message>> sendMessage(@PathVariable UUID roomId,
                                            @RequestBody MessageRequest request) {
        return new SendMessageCommand(messageService, roomId, request).execute();
    }

    @GetMapping(value = "/{roomId}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessages(@PathVariable UUID roomId) {
        return chatSseService.subscribeToRoom(roomId);
    }

    @PutMapping("{roomId}/message/{messageId}")
    public ResponseEntity<ApiResponse<Message>> editMessage(@PathVariable UUID roomId,
                                            @PathVariable UUID messageId,
                                            @RequestBody MessageRequest request) {
        return new EditMessageCommand(messageService, messageId, request).execute();
    }

    @DeleteMapping("{roomId}/message/{messageId}")
    public ResponseEntity<ApiResponse<Message>> deleteMessage(@PathVariable UUID roomId,
                                              @PathVariable UUID messageId,
                                              @RequestBody MessageRequest request) {
        return new DeleteMessageCommand(messageService, messageId, request).execute();
    }
}