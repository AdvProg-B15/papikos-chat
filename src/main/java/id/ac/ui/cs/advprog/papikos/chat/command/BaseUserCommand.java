// Java
package id.ac.ui.cs.advprog.papikos.chat.command;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import id.ac.ui.cs.advprog.papikos.chat.util.AuthenticationUtil;

public abstract class BaseUserCommand<T> implements ChatCommand<T> {

    protected final Authentication authentication;

    protected BaseUserCommand(Authentication authentication) {
        this.authentication = authentication;
    }

    protected UUID getUserId() {
        return AuthenticationUtil.getUserIdFromAuthentication(authentication);
    }
}