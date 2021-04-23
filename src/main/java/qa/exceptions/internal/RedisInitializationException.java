package qa.exceptions.internal;

import java.io.Serial;

public class RedisInitializationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -7295364113949581877L;

    public RedisInitializationException(String message) {
        super(message);
    }
}