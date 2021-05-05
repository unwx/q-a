package qa.exceptions.rest;

import java.io.Serial;

public class UnauthorizedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3269301501529818874L;

    public UnauthorizedException(String message) {
        super(message);
    }
}
