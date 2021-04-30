package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import qa.dao.Domain;
import qa.dao.database.components.FieldDataSetterExtractor;
import qa.exceptions.domain.SettersInitializationException;

import java.util.HashMap;

@Component
public class PropertySetterFactory {

    private static final Logger logger = LogManager.getLogger(PropertySetterFactory.class);
    protected PropertySetterFactory() {}

    private final HashMap<String, PropertySetter> propertySetters = new HashMap<>();

    public <E extends FieldDataSetterExtractor & Domain> PropertySetter getSetter(E target) {
        String targetClazzName = target.getClass().getName();
        return propertySetters.computeIfAbsent(targetClazzName, c -> createPropertySetter(target));
    }

    private <E extends FieldDataSetterExtractor & Domain> PropertySetter createPropertySetter(E target) {
        try {
            return new PropertySetterImpl<>(target.getClass(), target); // TODO REFACTOR

        } catch (SettersInitializationException ex) {
            settersInitializationException(ex, target.getClass().getName());
        }
        throw unexpectedException(target.getClass().getName());
    }

    private void settersInitializationException(Exception ex, String clazzName) {
        ex.printStackTrace();
        String message =
                """
                [setter factory error]: property setter creation failed ->\s\
                class %s ->\s\
                cannot create setters. check if your entity has setters and their ratio in extractSettersField\
                """.formatted(clazzName);
        logger.fatal(message);
    }

    private RuntimeException unexpectedException(String clazzName) {
        logger.fatal("[setter factory error]: property setter creation failed -> unexpected error " + clazzName);
        return new IllegalArgumentException();
    }
}
