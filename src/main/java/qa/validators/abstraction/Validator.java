package qa.validators.abstraction;

public abstract class Validator {
    protected static final String validationExceptionAsStr = "[Validation exception]";
    protected static final String unsuccessful = "[validation unsuccessful]: ";
    protected String formatMessage(String message) {
        return validationExceptionAsStr + " -> " + "[" + message + "]";
    }
}
