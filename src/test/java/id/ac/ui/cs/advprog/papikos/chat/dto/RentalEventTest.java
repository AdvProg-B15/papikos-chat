package id.ac.ui.cs.advprog.papikos.chat.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RentalEventTest {

    @Test
    void testAllArgsConstructor() {
        RentalEvent event = new RentalEvent("user1", "owner1");

        assertEquals("user1", event.getUserId());
        assertEquals("owner1", event.getKosOwnerId());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        RentalEvent event = new RentalEvent();
        event.setUserId("user2");
        event.setKosOwnerId("owner2");

        assertEquals("user2", event.getUserId());
        assertEquals("owner2", event.getKosOwnerId());
    }

    @Test
    void testEqualsAndHashCode() {
        RentalEvent event1 = new RentalEvent("user3", "owner3");
        RentalEvent event2 = new RentalEvent("user3", "owner3");

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }
}