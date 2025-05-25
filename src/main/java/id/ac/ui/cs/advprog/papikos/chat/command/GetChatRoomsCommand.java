package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetChatRoomsCommand implements ChatCommand<List<ChatRoom>> {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRequest request;

    public GetChatRoomsCommand(ChatRoomService chatRoomService, ChatRoomRequest request) {
        this.chatRoomService = chatRoomService;
        this.request = request;
    }

    @Override
    public ResponseEntity<ApiResponse<List<ChatRoom>>> execute() {
        List<ChatRoom> chatrooms = chatRoomService.getAllChatRoomsForUser(request.getSenderId());
        ApiResponse<List<ChatRoom>> response = ApiResponse.<List<ChatRoom>>builder()
                .status(HttpStatus.OK)
                .message("Chat rooms fetched successfully")
                .data(chatrooms)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}