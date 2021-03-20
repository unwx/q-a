package qa.exceptions.service.internal;

import java.io.Serial;

public class AuthorNotExistException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -748504416847755028L;

    public AuthorNotExistException(String message) {
        super(message);
    }

    public AuthorNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorNotExistException(Throwable cause) {
        super(cause);
    }
}
