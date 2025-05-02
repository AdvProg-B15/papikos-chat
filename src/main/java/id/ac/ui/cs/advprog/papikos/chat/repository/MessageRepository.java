package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomIdOrderByCreatedAtAsc(Long validRoomId);

    List<Message> findByRoomIdOrderByCreatedAtDesc(Long roomId);

    Optional<Message> findFirstByRoomIdOrderByCreatedAtDesc(Long roomId);
}