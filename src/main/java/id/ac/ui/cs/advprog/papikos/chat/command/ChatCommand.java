package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ChatCommand<T> {
    ResponseEntity<ApiResponse<T>> execute();
}