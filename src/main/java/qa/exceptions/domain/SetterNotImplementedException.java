package qa.exceptions.domain;

import java.io.Serial;

public class SetterNotImplementedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4186788443628717142L;

    public SetterNotImplementedException(String message) {
        super(message);
    }
}
