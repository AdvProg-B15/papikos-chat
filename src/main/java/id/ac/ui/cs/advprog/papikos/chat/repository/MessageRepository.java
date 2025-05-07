package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByRoomIdOrderByCreatedAtAsc(UUID validRoomId);

    List<Message> findByRoomIdOrderByCreatedAtDesc(UUID roomId);

    Optional<Message> findFirstByRoomIdOrderByCreatedAtDesc(UUID roomId);
}