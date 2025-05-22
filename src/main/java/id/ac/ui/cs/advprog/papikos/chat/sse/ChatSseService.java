package id.ac.ui.cs.advprog.papikos.chat.sse;

import id.ac.ui.cs.advprog.papikos.chat.command.GetMessagesCommand;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ChatSseService {

    private final MessageService messageService;
    private final ConcurrentHashMap<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    @Autowired
    public ChatSseService(MessageService messageService) {
        this.messageService = messageService;
    }

    public SseEmitter subscribeToRoom(UUID roomId) {
        SseEmitter emitter = new SseEmitter(3600000L);

        addEmitter(roomId, emitter);

        try {
            ApiResponse<List<Message>> response = new GetMessagesCommand(messageService, roomId, "asc").execute();
            emitter.send(SseEmitter.event().name("initial").data(response.getData()));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        emitter.onCompletion(() -> removeEmitter(roomId, emitter));
        emitter.onTimeout(() -> removeEmitter(roomId, emitter));

        return emitter;
    }

    public void sendMessageToRoom(UUID roomId) {
        List<SseEmitter> roomEmitters = emitters.get(roomId);
        if (roomEmitters != null) {
            ApiResponse<List<Message>> response = new GetMessagesCommand(messageService, roomId, "asc").execute();
            for (SseEmitter emitter : roomEmitters) {
                try {
                    emitter.send(SseEmitter.event().name("update").data(response.getData()));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    removeEmitter(roomId, emitter);
                }
            }
        }
    }

    private void addEmitter(UUID roomId, SseEmitter emitter) {
        emitters.computeIfAbsent(roomId, key -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    private void removeEmitter(UUID roomId, SseEmitter emitter) {
        List<SseEmitter> roomEmitters = emitters.get(roomId);
        if (roomEmitters != null) {
            roomEmitters.remove(emitter);
        }
    }
}