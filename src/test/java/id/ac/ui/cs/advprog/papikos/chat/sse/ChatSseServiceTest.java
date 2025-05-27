package id.ac.ui.cs.advprog.papikos.chat.sse;

import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatSseServiceTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private ChatSseService chatSseService;

    private UUID roomId1;
    private UUID senderId1;
    private Message message1;
    private List<Message> sampleMessages;

    @BeforeEach
    void setUp() {
        roomId1 = UUID.randomUUID();
        senderId1 = UUID.randomUUID();

        message1 = new Message(roomId1, senderId1, "Test Message");
        sampleMessages = Collections.singletonList(message1);

        ReflectionTestUtils.setField(chatSseService, "emitters", new ConcurrentHashMap<UUID, List<SseEmitter>>());
    }

    @Test
    void subscribeToRoom_shouldAddEmitterAndAttemptToGetInitialMessages() throws IOException {
        when(messageService.getMessagesByRoomAsc(roomId1)).thenReturn(sampleMessages);

        SseEmitter emitter = chatSseService.subscribeToRoom(roomId1);

        assertNotNull(emitter, "Emitter should not be null.");
        verify(messageService).getMessagesByRoomAsc(roomId1); // Verifikasi GetMessagesCommand memanggil service

        @SuppressWarnings("unchecked")
        ConcurrentHashMap<UUID, List<SseEmitter>> emittersMap =
                (ConcurrentHashMap<UUID, List<SseEmitter>>) ReflectionTestUtils.getField(chatSseService, "emitters");
        assertNotNull(emittersMap, "Emitters map should not be null.");
        assertTrue(emittersMap.containsKey(roomId1), "Emitters map should contain the room ID.");
        assertEquals(1, emittersMap.get(roomId1).size(), "Emitter list for the room should contain one emitter.");
        assertSame(emitter, emittersMap.get(roomId1).get(0), "The returned emitter should be the one in the map.");
    }

    @Test
    void subscribeToRoom_whenInitialSendFailsWithIOException_shouldCompleteEmitterWithError() throws IOException {
        when(messageService.getMessagesByRoomAsc(roomId1)).thenReturn(sampleMessages);

        SseEmitter emitter = chatSseService.subscribeToRoom(roomId1);
        assertNotNull(emitter);
        verify(messageService).getMessagesByRoomAsc(roomId1);
    }


    @Test
    void sendMessageToRoom_whenRoomHasNoEmitters_shouldNotThrowExceptionAndNotCallMessageService() {
        assertDoesNotThrow(() -> chatSseService.sendMessageToRoom(roomId1),
                "sendMessageToRoom should not throw an exception if no emitters exist for the room.");

        verify(messageService, never()).getMessagesByRoomAsc(roomId1);
    }

    @Test
    void sendMessageToRoom_whenRoomHasEmitters_shouldAttemptToSendUpdate() throws IOException {
        SseEmitter spyEmitter = spy(new SseEmitter(3600000L));
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<UUID, List<SseEmitter>> emittersMap =
                (ConcurrentHashMap<UUID, List<SseEmitter>>) ReflectionTestUtils.getField(chatSseService, "emitters");
        CopyOnWriteArrayList<SseEmitter> roomEmittersList = new CopyOnWriteArrayList<>(Collections.singletonList(spyEmitter));
        emittersMap.put(roomId1, roomEmittersList);

        when(messageService.getMessagesByRoomAsc(roomId1)).thenReturn(sampleMessages);

        chatSseService.sendMessageToRoom(roomId1);

        verify(messageService).getMessagesByRoomAsc(roomId1);
        verify(spyEmitter).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void sendMessageToRoom_whenEmitterSendFails_shouldCompleteEmitterWithErrorAndRemove() throws IOException {
        SseEmitter spyEmitter = spy(new SseEmitter(3600000L));
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<UUID, List<SseEmitter>> emittersMap =
                (ConcurrentHashMap<UUID, List<SseEmitter>>) ReflectionTestUtils.getField(chatSseService, "emitters");
        CopyOnWriteArrayList<SseEmitter> roomEmittersList = new CopyOnWriteArrayList<>(Collections.singletonList(spyEmitter));
        emittersMap.put(roomId1, roomEmittersList);

        when(messageService.getMessagesByRoomAsc(roomId1)).thenReturn(sampleMessages);
        IOException simulatedError = new IOException("Simulated send error");
        doThrow(simulatedError).when(spyEmitter).send(any(SseEmitter.SseEventBuilder.class));

        chatSseService.sendMessageToRoom(roomId1);

        verify(messageService).getMessagesByRoomAsc(roomId1);
        verify(spyEmitter).send(any(SseEmitter.SseEventBuilder.class)); // send dipanggil
        verify(spyEmitter).completeWithError(simulatedError);        // completeWithError dipanggil
        assertTrue(roomEmittersList.isEmpty(), "Emitter should be removed from the list after send failure.");
    }
}