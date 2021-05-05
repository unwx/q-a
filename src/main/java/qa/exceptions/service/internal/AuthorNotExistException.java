package qa.exceptions.service.internal;

import java.io.Serial;

public class AuthorNotExistException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -748504416847755028L;

    public AuthorNotExistException(String message) {
        super(message);
    }
}
