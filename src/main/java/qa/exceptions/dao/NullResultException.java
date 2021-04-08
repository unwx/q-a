package qa.exceptions.dao;

import java.io.Serial;

public class NullResultException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2954044266834018219L;

    public NullResultException() {
    }

    public NullResultException(String message) {
        super(message);
    }
}
