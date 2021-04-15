package qa.exceptions.internal;

import java.io.Serial;

public class PasswordEncryptorInitializationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4512882883452618571L;

    public PasswordEncryptorInitializationException(String message) {
        super(message);
    }
}
