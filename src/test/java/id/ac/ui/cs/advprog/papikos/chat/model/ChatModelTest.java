package id.ac.ui.cs.advprog.papikos.chat.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ChatModelTest {

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Chat chat = new Chat(1L, "Andi", "Halo!", now);

        assertThat(chat.getId()).isEqualTo(1L);
        assertThat(chat.getSender()).isEqualTo("Andi");
        assertThat(chat.getMessage()).isEqualTo("Halo!");
        assertThat(chat.getTimestamp()).isEqualTo(now);
    }

    @Test
    void testSettersAndGetters() {
        Chat chat = new Chat();

        chat.setId(10L);
        chat.setSender("Budi");
        chat.setMessage("Tes");
        LocalDateTime customTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        chat.setTimestamp(customTime);

        assertThat(chat.getId()).isEqualTo(10L);
        assertThat(chat.getSender()).isEqualTo("Budi");
        assertThat(chat.getMessage()).isEqualTo("Tes");
        assertThat(chat.getTimestamp()).isEqualTo(customTime);
    }

    @Test
    void testPrePersistSetTimestampIfNull() {
        Chat chat = new Chat();
        chat.setSender("Cici");
        chat.setMessage("Auto timestamp");
        chat.setTimestamp(null);

        chat.prePersist();

        assertThat(chat.getTimestamp()).isNotNull();
    }

    @Test
    void testPrePersistDoesNotOverrideExistingTimestamp() {
        Chat chat = new Chat();
        LocalDateTime fixedTime = LocalDateTime.of(2022, 1, 1, 10, 0);
        chat.setTimestamp(fixedTime);

        chat.prePersist();

        assertThat(chat.getTimestamp()).isEqualTo(fixedTime);
    }
}
