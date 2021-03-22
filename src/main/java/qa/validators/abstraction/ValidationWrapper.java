package qa.validators.abstraction;

import org.jetbrains.annotations.Nullable;
import qa.validators.entities.*;

/**
 * <h3>fill in only the fields you need for validation</h3>
 * <h2>null = ignore</h2>
 */
public interface ValidationWrapper {
    @Nullable
    default ValidationStringField[] getStringFields() {
        return null;
    }

    @Nullable
    default ValidationNumberField[] getNumberFields() {
        return null;
    }

    @Nullable
    default ValidationObjectField[] getObjectFields() {
        return null;
    }

    @Nullable
    @SuppressWarnings("rawtypes")
    default ValidationAdditional[] getAdditional() {
        return null;
    }

    @Nullable
    default ValidationRegexField[] getRegexFields() {
        return null;
    }
}
