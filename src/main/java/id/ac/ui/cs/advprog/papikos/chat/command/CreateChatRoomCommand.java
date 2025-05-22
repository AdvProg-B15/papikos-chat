package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;

public class CreateChatRoomCommand implements ChatCommand<ChatRoom> {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRequest request;

    public CreateChatRoomCommand(ChatRoomService chatRoomService, ChatRoomRequest request) {
        this.chatRoomService = chatRoomService;
        this.request = request;
    }

    @Override
    public ApiResponse<ChatRoom> execute() {
        ChatRoom chatRoom = chatRoomService.createChatRoom(request.getSenderId(), request.getRecipientId());
        return ApiResponse.<ChatRoom>builder().created(chatRoom);
    }
}