package qa.validators.abstraction;

import org.jetbrains.annotations.Nullable;
import qa.validators.entities.ValidationNumberField;
import qa.validators.entities.ValidationObjectField;
import qa.validators.entities.ValidationStringField;

/**
 * <h3>fill in only the fields you need for validation</h3>
 * <h2>null = ignore</h2>
 */
public interface ValidatorEntity {
    @Nullable
    ValidationStringField[] getStringFields();

    @Nullable
    ValidationNumberField[] getNumberFields();

    @Nullable
    ValidationObjectField[] getObjectFields();
}
