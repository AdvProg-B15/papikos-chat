package id.ac.ui.cs.advprog.papikos.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.papikos.chat.dto.ChatRoomRequest;
import id.ac.ui.cs.advprog.papikos.chat.dto.MessageRequest;
import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import id.ac.ui.cs.advprog.papikos.chat.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ChatController chatController;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private MessageService messageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    public void testGetChatRoomsForUser() throws Exception {
        UUID senderId = UUID.randomUUID();
        ChatRoomRequest request = new ChatRoomRequest();
        request.setSenderId(senderId);

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(UUID.randomUUID())
                .user1Id(senderId)
                .user2Id(UUID.randomUUID())
                .build();

        List<ChatRoom> chatRoomList = Collections.singletonList(chatRoom);

        BDDMockito.given(chatRoomService.getAllChatRoomsForUser(senderId))
                .willReturn(chatRoomList);

        mockMvc.perform(get("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data[0].roomId").value(chatRoom.getRoomId().toString()));
    }

    @Test
    public void testCreateChatRoom() throws Exception {
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        ChatRoomRequest request = new ChatRoomRequest();
        request.setSenderId(senderId);
        request.setRecipientId(recipientId);

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(UUID.randomUUID())
                .user1Id(senderId)
                .user2Id(recipientId)
                .build();

        BDDMockito.given(chatRoomService.createChatRoom(senderId, recipientId))
                .willReturn(chatRoom);

        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Resource created successfully"))
                .andExpect(jsonPath("$.data.roomId").value(chatRoom.getRoomId().toString()));
    }

    @Test
    public void testSendMessage() throws Exception {
        UUID roomId = UUID.randomUUID();
        UUID senderUserId = UUID.randomUUID();
        String content = "Hello, World!";

        MessageRequest request = new MessageRequest();
        request.setSenderUserId(senderUserId);
        request.setContent(content);

        Message message = Message.builder()
                .messageId(UUID.randomUUID())
                .roomId(roomId)
                .senderUserId(senderUserId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        BDDMockito.given(messageService.sendMessage(roomId, senderUserId, content))
                .willReturn(message);

        mockMvc.perform(post("/chat/" + roomId + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Resource created successfully"))
                .andExpect(jsonPath("$.data.messageId").value(message.getMessageId().toString()));
    }

    @Test
    public void testGetMessagesAsc() throws Exception {
        UUID roomId = UUID.randomUUID();
        Message message = Message.builder()
                .messageId(UUID.randomUUID())
                .roomId(roomId)
                .content("Ascending Order")
                .createdAt(LocalDateTime.now())
                .build();

        List<Message> messages = Collections.singletonList(message);

        BDDMockito.given(messageService.getMessagesByRoomAsc(roomId))
                .willReturn(messages);

        mockMvc.perform(get("/chat/" + roomId + "/messages")
                        .param("order", "asc"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data[0].messageId").value(message.getMessageId().toString()));
    }

    @Test
    public void testGetMessagesDesc() throws Exception {
        UUID roomId = UUID.randomUUID();
        Message message = Message.builder()
                .messageId(UUID.randomUUID())
                .roomId(roomId)
                .content("Descending Order")
                .createdAt(LocalDateTime.now())
                .build();

        List<Message> messages = Collections.singletonList(message);

        BDDMockito.given(messageService.getMessagesByRoomDesc(roomId))
                .willReturn(messages);

        mockMvc.perform(get("/chat/" + roomId + "/messages")
                        .param("order", "desc"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data[0].messageId").value(message.getMessageId().toString()));
    }

    @Test
    public void testEditMessage() throws Exception {
        UUID roomId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID senderUserId = UUID.randomUUID();
        String newContent = "Edited Message";

        MessageRequest request = new MessageRequest();
        request.setSenderUserId(senderUserId);
        request.setContent(newContent);

        Message editedMessage = Message.builder()
                .messageId(messageId)
                .roomId(roomId)
                .senderUserId(senderUserId)
                .content(newContent)
                .createdAt(LocalDateTime.now())
                .build();

        BDDMockito.given(messageService.editMessageContent(messageId, senderUserId, newContent))
                .willReturn(editedMessage);

        mockMvc.perform(put("/chat/" + roomId + "/message/" + messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.messageId").value(messageId.toString()))
                .andExpect(jsonPath("$.data.content").value(newContent));
    }

    @Test
    public void testDeleteMessage() throws Exception {
        UUID roomId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID senderUserId = UUID.randomUUID();

        MessageRequest request = new MessageRequest();
        request.setSenderUserId(senderUserId);

        Message deletedMessage = Message.builder()
                .messageId(messageId)
                .roomId(roomId)
                .senderUserId(senderUserId)
                .content("Deleted")
                .createdAt(LocalDateTime.now())
                .build();

        BDDMockito.given(messageService.markMessageAsDeleted(messageId, senderUserId))
                .willReturn(deletedMessage);

        mockMvc.perform(delete("/chat/" + roomId + "/message/" + messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.messageId").value(messageId.toString()));
    }
}