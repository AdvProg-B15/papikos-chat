package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;

import java.util.List;
import java.util.UUID;

public class GetMessagesCommand implements ChatCommand<List<Message>> {

    private final MessageService messageService;
    private final UUID roomId;
    private final String order;

    public GetMessagesCommand(MessageService messageService, UUID roomId, String order) {
        this.messageService = messageService;
        this.roomId = roomId;
        this.order = order;
    }

    @Override
    public ApiResponse<List<Message>> execute() {
        List<Message> messages;
        if ("desc".equalsIgnoreCase(order)) {
            messages = messageService.getMessagesByRoomDesc(roomId);
        } else {
            messages = messageService.getMessagesByRoomAsc(roomId);
        }
        return ApiResponse.<List<Message>>builder().ok(messages);
    }
}