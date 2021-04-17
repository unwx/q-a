package qa.exceptions.dao;

import java.io.Serial;

public class EntityAlreadyCreatedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -386865663280760636L;

    public EntityAlreadyCreatedException() {
    }

    public EntityAlreadyCreatedException(String message) {
        super(message);
    }
}
