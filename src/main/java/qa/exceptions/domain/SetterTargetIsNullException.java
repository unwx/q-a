package qa.exceptions.domain;

import java.io.Serial;

public class SetterTargetIsNullException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -3755585780226319049L;

    public SetterTargetIsNullException(String message) {
        super(message);
    }

    public SetterTargetIsNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetterTargetIsNullException(Throwable cause) {
        super(cause);
    }
}
