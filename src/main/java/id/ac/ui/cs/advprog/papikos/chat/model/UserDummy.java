package id.ac.ui.cs.advprog.papikos.chat.model;

import lombok.Data;

@Data
public class UserDummy {
    private Long id;
    private String username;
    private Role role;

    public enum Role {
        USER, KOS_OWNER
    }
}
