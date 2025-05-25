package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CreateChatRoomCommand implements ChatCommand<ChatRoom> {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRequest request;

    public CreateChatRoomCommand(ChatRoomService chatRoomService, ChatRoomRequest request) {
        this.chatRoomService = chatRoomService;
        this.request = request;
    }

    @Override
    public ResponseEntity<ApiResponse<ChatRoom>> execute() {
        ChatRoom chatRoom = chatRoomService.createChatRoom(request.getSenderId(), request.getRecipientId());
        ApiResponse<ChatRoom> response = ApiResponse.<ChatRoom>builder()
                .status(HttpStatus.CREATED)
                .message("Chat Room created successfully!")
                .data(chatRoom)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}