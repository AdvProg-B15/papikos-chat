package id.ac.ui.cs.advprog.papikos.chat.repository;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomsRepository extends JpaRepository<ChatRooms, Long> {
    Optional<ChatRooms> findByUser1IdAndUser2Id(Long orderedUserId, Long orderedUserId1);

    List<ChatRooms> findByUser1IdOrUser2Id(Long userId, Long userId1);
}