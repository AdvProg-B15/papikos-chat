package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long> {

    List<Messages> findByRoomIdOrderByCreatedAtAsc(Long validRoomId);

    List<Messages> findByRoomIdOrderByCreatedAtDesc(Long roomId);

    Optional<Messages> findFirstByRoomIdOrderByCreatedAtDesc(Long roomId);
}