package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<ApiResponse<Message>> execute() {
        Message message = messageService.sendMessage(roomId, request.getSenderUserId(), request.getContent());
        ApiResponse<Message> response = ApiResponse.<Message>builder()
                .status(HttpStatus.CREATED)
                .message("Message sent successfully")
                .data(message)
                .build();
        return ResponseEntity.ok(response);
    }
}