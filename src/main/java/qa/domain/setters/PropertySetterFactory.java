package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import qa.dao.database.components.FieldDataSetterExtractor;
import qa.exceptions.domain.SettersInitializationException;

import java.util.HashMap;

@Component
public class PropertySetterFactory {

    private final HashMap<String, PropertySetter> propertySetters = new HashMap<>();

    private static final Logger logger = LogManager.getLogger(PropertySetterFactory.class);
    private static final String ERR_CREATION =
                """
                property setter creation failed ->\s\
                class %s ->\s\
                cannot create setters. check if your entity has setters and their ratio in extractSettersField\
                """;

    protected PropertySetterFactory() {}

    public PropertySetter getSetter(FieldDataSetterExtractor target) {
        final String targetClazzName = target.getClass().getName();
        return propertySetters.computeIfAbsent(targetClazzName, c -> createPropertySetter(target));
    }

    private PropertySetter createPropertySetter(FieldDataSetterExtractor target) {
        try {
            return new PropertySetterImpl(target.getClass(), target);
        } catch (SettersInitializationException ex) {
            final String message = ERR_CREATION.formatted(target.getClassName());
            logger.fatal(message);
            throw new IllegalArgumentException(message);
        }
    }
}
