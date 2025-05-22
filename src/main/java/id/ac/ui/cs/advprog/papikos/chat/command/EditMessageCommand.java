package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;

import java.util.UUID;

public class EditMessageCommand implements ChatCommand<Message> {

    private final MessageService messageService;
    private final UUID messageId;
    private final MessageRequest request;

    public EditMessageCommand(MessageService messageService, UUID messageId, MessageRequest request) {
        this.messageService = messageService;
        this.messageId = messageId;
        this.request = request;
    }

    @Override
    public ApiResponse<Message> execute() {
        Message editedMessage = messageService.editMessageContent(messageId, request.getSenderUserId(), request.getContent());
        return ApiResponse.<Message>builder().ok(editedMessage);
    }
}