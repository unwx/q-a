package qa.exceptions.domain;

import java.io.Serial;

public class SettersInitializationException extends Exception {
    @Serial
    private static final long serialVersionUID = 3006665085721170928L;

    public SettersInitializationException(String message) {
        super(message);
    }

    public SettersInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SettersInitializationException(Throwable cause) {
        super(cause);
    }
}
