package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.exceptions.domain.SetterNotImplementedException;
import qa.exceptions.domain.SetterTargetIsNullException;
import qa.exceptions.domain.SettersInitializationException;

import java.io.Serial;
import java.util.HashMap;

public class PropertySetterImpl implements PropertySetter {

    private final Logger logger = LogManager.getLogger(PropertySetterImpl.class);

    private final HashMap<String, ISetter<FieldDataSetterExtractor>> setters;

    public PropertySetterImpl(Class<? extends FieldDataSetterExtractor> target, FieldDataSetterExtractor emptyObject) throws SettersInitializationException {
        this.setters = SettersInitializer.init(target, emptyObject);
    }

    @Override
    public void setAll(FieldDataSetterExtractor object, String[] names, Object[] values) {
        for (int i = 0; i < names.length; i++) {
            try {
                ISetter<FieldDataSetterExtractor> s = setters.get(names[i]);
                s.set(object, values[i]);
            } catch (NullPointerException ex) {
                nullPointerExceptionProcess(object, names[i]);
            }
        }
    }

    @Override
    public void set(FieldDataSetterExtractor object, String name, Object value) {
        try {
            ISetter<FieldDataSetterExtractor> s = setters.get(name);
            s.set(object, value);
        } catch (NullPointerException ex) {
            nullPointerExceptionProcess(object, name);
        }
    }

    private void nullPointerExceptionProcess(FieldDataSetterExtractor object, String name) {
        if (object == null) {
            String message = "NullPointerException -> setter target is null. -> FieldDataSetterExtractor = null.";
            logger.error(message);
            throw new SetterTargetIsNullException(message);
        } else {
            String message = "NullPointerException -> setter " + name + " not exist/implemented. ";
            logger.error(message);
            throw new SetterNotImplementedException(message);
        }
    }

    private static class SettersInitializer {

        public static HashMap<String, ISetter<FieldDataSetterExtractor>> init(Class<? extends FieldDataSetterExtractor> clazz, FieldDataSetterExtractor obj) throws SettersInitializationException {
            return new HashMap<>() {

                @Serial
                private static final long serialVersionUID = 7124974512179832211L;

                {
                    SetterField[] fields = obj.extractSettersField();
                    for (SetterField field : fields) {
                        try {
                            put(field.getName(), SetterMethodBuilder.getSetter(clazz, field.getName(), field.getType()));
                        } catch (Throwable throwable) {
                            throw new SettersInitializationException(throwable.getMessage(), throwable.getCause());
                        }
                    }
                }
            };
        }
    }
}
