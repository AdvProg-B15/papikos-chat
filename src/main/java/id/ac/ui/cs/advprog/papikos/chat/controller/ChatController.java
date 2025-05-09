package id.ac.ui.cs.advprog.papikos.chat.controller;

import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: integrasi dengan module lain agar userId tidak usah ditaro di body request
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    @Autowired
    public ChatController(ChatRoomService chatRoomService, MessageService messageService) {
        this.chatRoomService = chatRoomService;
        this.messageService = messageService;
    }

    @GetMapping
    public ApiResponse<List<ChatRoom>> getChatRoomsForUser(@RequestBody ChatRoomRequest request) {
        List<ChatRoom> chatrooms = chatRoomService.getAllChatRoomsForUser(request.getSenderId());
        return ApiResponse.<List<ChatRoom>>builder().ok(chatrooms);
    }

    @PostMapping
    public ApiResponse<ChatRoom> createChatRoom(@RequestBody ChatRoomRequest request) {
        ChatRoom chatRoom = chatRoomService.createChatRoom(request.getSenderId(), request.getRecipientId());
        return ApiResponse.<ChatRoom>builder().created(chatRoom);
    }

    @PostMapping("/{roomId}/messages")
    public ApiResponse<Message> sendMessage(@PathVariable UUID roomId,
                                            @RequestBody MessageRequest request) {
        Message message = messageService.sendMessage(roomId, request.getSenderUserId(), request.getContent());
        return ApiResponse.<Message>builder().created(message);
    }

    @GetMapping("/{roomId}/messages")
    public ApiResponse<List<Message>> getMessages(@PathVariable UUID roomId,
                                                  @RequestParam(defaultValue = "asc") String order) {
        List<Message> messages;
        if ("desc".equalsIgnoreCase(order)) {
            messages = messageService.getMessagesByRoomDesc(roomId);
        } else {
            messages = messageService.getMessagesByRoomAsc(roomId);
        }
        return ApiResponse.<List<Message>>builder().ok(messages);
    }

    @PutMapping("{roomId}/message/{messageId}")
    public ApiResponse<Message> editMessage(
            @PathVariable("roomId") UUID roomId,
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageRequest request) {

        Message editedMessage = messageService.editMessageContent(messageId, request.getSenderUserId(), request.getContent());

        return ApiResponse.<Message>builder().ok(editedMessage);
    }

    @DeleteMapping("{roomId}/message/{messageId}")
    public ApiResponse<Message> deleteMessage(
            @PathVariable("roomId") UUID roomId,
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageRequest request) {

        Message deletedMessage = messageService.markMessageAsDeleted(messageId, request.getSenderUserId());
        return ApiResponse.<Message>builder().ok(deletedMessage);
    }

}