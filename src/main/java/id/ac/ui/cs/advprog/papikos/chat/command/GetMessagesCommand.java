package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    // Java
    @Override
    public ResponseEntity<ApiResponse<List<Message>>> execute() {
        List<Message> messages;
        if ("desc".equalsIgnoreCase(order)) {
            messages = messageService.getMessagesByRoomDesc(roomId);
        } else {
            messages = messageService.getMessagesByRoomAsc(roomId);
        }

        ApiResponse<List<Message>> response = ApiResponse.<List<Message>>builder()
                .status(HttpStatus.OK)
                .message("Messages fetched successfully")
                .data(messages)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}