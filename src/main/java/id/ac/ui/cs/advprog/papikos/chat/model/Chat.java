package id.ac.ui.cs.advprog.papikos.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    private Long id;
    private String sender;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
}
