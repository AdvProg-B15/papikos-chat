package id.ac.ui.cs.advprog.papikos.chat.listener;

import id.ac.ui.cs.advprog.papikos.chat.dto.RentalEvent;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatEventListenerTest {

    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private ChatEventListener chatEventListener;

    private final PrintStream originalSystemOut = System.out;
    private final PrintStream originalSystemErr = System.err;
    private ByteArrayOutputStream systemOutContent;
    private ByteArrayOutputStream systemErrContent;

    @BeforeEach
    void setUp() {
        systemOutContent = new ByteArrayOutputStream();
        systemErrContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutContent));
        System.setErr(new PrintStream(systemErrContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalSystemOut);
        System.setErr(originalSystemErr);
    }

    @Test
    void handleRentalCreatedEvent_withValidUUIDs_shouldCallCreateChatRoom() {
        String userIdStr = UUID.randomUUID().toString();
        String kosOwnerIdStr = UUID.randomUUID().toString();
        RentalEvent event = new RentalEvent(userIdStr, kosOwnerIdStr);

        UUID expectedUserId = UUID.fromString(userIdStr);
        UUID expectedKosOwnerId = UUID.fromString(kosOwnerIdStr);

        chatEventListener.handleRentalCreatedEvent(event);


        verify(chatRoomService).createChatRoom(expectedUserId, expectedKosOwnerId);

        assertTrue(systemOutContent.toString().contains("ChatService: userId=" + userIdStr + ", kosOwnerId=" + kosOwnerIdStr));
        assertTrue(systemOutContent.toString().contains("Chat room berhasil dibuat untuk userId " + expectedUserId + " dan kosOwnerId " + expectedKosOwnerId));
        assertTrue(systemErrContent.toString().isEmpty(), "System.err should be empty for valid input.");
    }

    @Test
    void handleRentalCreatedEvent_withInvalidUserIdFormat_shouldCatchIllegalArgumentExceptionAndNotCallService() {
        String invalidUserIdStr = "not-a-uuid";
        String kosOwnerIdStr = UUID.randomUUID().toString();
        RentalEvent event = new RentalEvent(invalidUserIdStr, kosOwnerIdStr);

        chatEventListener.handleRentalCreatedEvent(event);


        verify(chatRoomService, never()).createChatRoom(any(UUID.class), any(UUID.class));

        assertTrue(systemOutContent.toString().contains("ChatService: userId=" + invalidUserIdStr + ", kosOwnerId=" + kosOwnerIdStr));
        String errOutput = systemErrContent.toString();
        assertTrue(errOutput.contains("Terjadi kesalahan pada format UUID:"), "Error message for UUID format should be present.");
        assertTrue(errOutput.contains(invalidUserIdStr) || errOutput.contains("Invalid UUID string"), "Error message should mention the invalid UUID.");
    }

    @Test
    void handleRentalCreatedEvent_withInvalidKosOwnerIdFormat_shouldCatchIllegalArgumentExceptionAndNotCallService() {
        String userIdStr = UUID.randomUUID().toString();
        String invalidKosOwnerIdStr = "definitely-not-a-uuid";
        RentalEvent event = new RentalEvent(userIdStr, invalidKosOwnerIdStr);

        chatEventListener.handleRentalCreatedEvent(event);

        verify(chatRoomService, never()).createChatRoom(any(UUID.class), any(UUID.class));

        assertTrue(systemOutContent.toString().contains("ChatService: userId=" + userIdStr + ", kosOwnerId=" + invalidKosOwnerIdStr));
        String errOutput = systemErrContent.toString();
        assertTrue(errOutput.contains("Terjadi kesalahan pada format UUID:"), "Error message for UUID format should be present.");
        assertTrue(errOutput.contains(invalidKosOwnerIdStr) || errOutput.contains("Invalid UUID string"), "Error message should mention the invalid UUID.");
    }

    @Test
    void handleRentalCreatedEvent_whenCreateChatRoomThrowsException_shouldStillHandleGracefully() {
        String userIdStr = UUID.randomUUID().toString();
        String kosOwnerIdStr = UUID.randomUUID().toString();
        RentalEvent event = new RentalEvent(userIdStr, kosOwnerIdStr);

        UUID expectedUserId = UUID.fromString(userIdStr);
        UUID expectedKosOwnerId = UUID.fromString(kosOwnerIdStr);

        doThrow(new RuntimeException("Database error or other service failure"))
                .when(chatRoomService).createChatRoom(expectedUserId, expectedKosOwnerId);


        assertThrows(RuntimeException.class, () -> {
            chatEventListener.handleRentalCreatedEvent(event);
        });


        verify(chatRoomService).createChatRoom(expectedUserId, expectedKosOwnerId);

        assertTrue(systemOutContent.toString().contains("ChatService: userId=" + userIdStr + ", kosOwnerId=" + kosOwnerIdStr));
        assertFalse(systemOutContent.toString().contains("Chat room berhasil dibuat"));
        assertTrue(systemErrContent.toString().isEmpty(), "System.err should be empty as the exception is not caught by the listener itself.");
    }
}