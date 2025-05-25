// Java
package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public class GetChatRoomsCommand extends BaseUserCommand<List<ChatRoom>> {

    private final ChatRoomService chatRoomService;

    public GetChatRoomsCommand(Authentication authentication, ChatRoomService chatRoomService) {
        super(authentication);
        this.chatRoomService = chatRoomService;
    }

    @Override
    public ResponseEntity<ApiResponse<List<ChatRoom>>> execute() {
        UUID userId = getUserId();
        List<ChatRoom> chatrooms = chatRoomService.getAllChatRoomsForUser(userId);
        ApiResponse<List<ChatRoom>> response = ApiResponse.<List<ChatRoom>>builder()
                .status(HttpStatus.OK)
                .message("Chat rooms fetched successfully")
                .data(chatrooms)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}