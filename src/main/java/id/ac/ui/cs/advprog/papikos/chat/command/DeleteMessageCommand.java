package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;

import java.util.UUID;

public class DeleteMessageCommand implements ChatCommand<Message> {

    private final MessageService messageService;
    private final UUID messageId;
    private final MessageRequest request;

    public DeleteMessageCommand(MessageService messageService, UUID messageId, MessageRequest request) {
        this.messageService = messageService;
        this.messageId = messageId;
        this.request = request;
    }

    @Override
    public ApiResponse<Message> execute() {
        Message deletedMessage = messageService.markMessageAsDeleted(messageId, request.getSenderUserId());
        return ApiResponse.<Message>builder().ok(deletedMessage);
    }
}