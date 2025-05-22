package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;

import java.util.List;

public class GetChatRoomsCommand implements ChatCommand<List<ChatRoom>> {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRequest request;

    public GetChatRoomsCommand(ChatRoomService chatRoomService, ChatRoomRequest request) {
        this.chatRoomService = chatRoomService;
        this.request = request;
    }

    @Override
    public ApiResponse<List<ChatRoom>> execute() {
        List<ChatRoom> chatrooms = chatRoomService.getAllChatRoomsForUser(request.getSenderId());
        return ApiResponse.<List<ChatRoom>>builder().ok(chatrooms);
    }
}