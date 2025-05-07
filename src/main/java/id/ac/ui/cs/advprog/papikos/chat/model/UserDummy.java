package id.ac.ui.cs.advprog.papikos.chat.model;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDummy {
    private UUID id;
    private String username;
    private Role role;

    public enum Role {
        USER, KOS_OWNER
    }
}
