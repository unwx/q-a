package qa.validator.entities;

import qa.validator.additional.AdditionalValidator;

public class ValidationAdditional<T> {

    private final T target;
    private final AdditionalValidator<T> additionalValidator;

    public ValidationAdditional(T target,
                                AdditionalValidator<T> additionalValidator) {
        this.target = target;
        this.additionalValidator = additionalValidator;
    }

    public T getTarget() {
        return target;
    }

    public AdditionalValidator<T> getAdditionalValidator() {
        return additionalValidator;
    }
}
