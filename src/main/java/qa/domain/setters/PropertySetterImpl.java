package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.dao.database.components.FieldDataSetterExtractor;
import qa.exceptions.domain.SetterNotImplementedException;
import qa.exceptions.domain.SetterTargetIsNullException;
import qa.exceptions.domain.SettersInitializationException;

import java.io.Serial;
import java.util.HashMap;

public class PropertySetterImpl implements PropertySetter {

    private final HashMap<String, ISetter<FieldDataSetterExtractor>> setters;
    private final FieldDataSetterExtractor entity;

    private static final Logger logger = LogManager.getLogger(PropertySetterImpl.class);
    private static final String ERR_TARGET_NULL = "NullPointerException -> setter target is null. -> FieldDataSetterExtractor = null";
    private static final String ERR_NOT_IMPLEMENTED = "NullPointerException -> setter %s not exist/implemented";

    public PropertySetterImpl(Class<? extends FieldDataSetterExtractor> target,
                              FieldDataSetterExtractor entity) throws SettersInitializationException {

        this.setters = SettersInitializer.init(target, entity);
        this.entity = entity;
    }

    @Override
    public void setAll(FieldDataSetterExtractor object, String[] names, Object[] values) {
        for (int i = 0; i < names.length; i++) {
            try {
                final ISetter<FieldDataSetterExtractor> setter = this.setters.get(names[i]);
                setter.set(object, values[i]);
            } catch (NullPointerException ex) {
                nullPointerExceptionProcess(object, names[i]);
            }
        }
    }

    @Override
    public void set(FieldDataSetterExtractor object, String name, Object value) {
        try {
            final ISetter<FieldDataSetterExtractor> setter = this.setters.get(name);
            setter.set(object, value);
        } catch (NullPointerException ex) {
            nullPointerExceptionProcess(object, name);
        }
    }

    @Override
    public FieldDataSetterExtractor entity() {
        return this.entity;
    }

    private void nullPointerExceptionProcess(FieldDataSetterExtractor object, String name) {
        if (object == null) {

            final String message = ERR_TARGET_NULL;
            logger.error(message);
            throw new SetterTargetIsNullException(message);

        } else {

            final String message = ERR_NOT_IMPLEMENTED.formatted(name);
            logger.error(message);
            throw new SetterNotImplementedException(message);

        }
    }

    private static class SettersInitializer {

        private SettersInitializer() {}

        public static HashMap<String, ISetter<FieldDataSetterExtractor>> init(Class<? extends FieldDataSetterExtractor> clazz,
                                                                              FieldDataSetterExtractor obj) throws SettersInitializationException {
            return new HashMap<>() {

                @Serial
                private static final long serialVersionUID = 7124974512179832211L;

                {
                    final SetterField[] fields = obj.extractSettersField();
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
