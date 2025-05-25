package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<ApiResponse<Message>> execute() {
        Message editedMessage = messageService.editMessageContent(messageId, request.getSenderUserId(), request.getContent());
        ApiResponse<Message> response = ApiResponse.<Message>builder()
                .status(HttpStatus.OK)
                .message("Message edited successfully")
                .data(editedMessage)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}