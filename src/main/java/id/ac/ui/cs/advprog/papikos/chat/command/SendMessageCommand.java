package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;

import java.util.UUID;

public class SendMessageCommand implements ChatCommand<Message> {

    private final MessageService messageService;
    private final UUID roomId;
    private final MessageRequest request;

    public SendMessageCommand(MessageService messageService, UUID roomId, MessageRequest request) {
        this.messageService = messageService;
        this.roomId = roomId;
        this.request = request;
    }

    @Override
    public ApiResponse<Message> execute() {
        Message message = messageService.sendMessage(roomId, request.getSenderUserId(), request.getContent());
        return ApiResponse.<Message>builder().created(message);
    }
}