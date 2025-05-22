package id.ac.ui.cs.advprog.papikos.chat.command;

import id.ac.ui.cs.advprog.papikos.chat.response.ApiResponse;

public interface ChatCommand<T> {
    ApiResponse<T> execute();
}