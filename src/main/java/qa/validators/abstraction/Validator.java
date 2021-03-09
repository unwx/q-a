package qa.validators.abstraction;

public abstract class Validator {
    protected final String validationExceptionAsStr = "[Validation exception]";
    protected String formatMessage(String message) {
        return validationExceptionAsStr + " -> " + "[" + message + "]";
    }
}
