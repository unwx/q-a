package qa.domain.setters;

import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.exceptions.domain.SettersInitializationException;

import java.io.Serial;
import java.util.HashMap;

public class PropertySetterImpl implements PropertySetter {

    private final HashMap<String, ISetter<FieldDataSetterExtractor>> setters;

    public PropertySetterImpl(Class<? extends FieldDataSetterExtractor> target, FieldDataSetterExtractor emptyObject) throws SettersInitializationException {
        this.setters = SettersInitializer.init(target, emptyObject);
    }

    @Override
    public void setAll(FieldDataSetterExtractor object, String[] names, Object[] values) {
        for (int i = 0; i < names.length; i++) {
            ISetter<FieldDataSetterExtractor> s = setters.get(names[i]);
            s.set(object, values[i]);
        }
    }

    @Override
    public void set(FieldDataSetterExtractor object, String name, Object value) {
        ISetter<FieldDataSetterExtractor> s = setters.get(name);
        s.set(object, value);
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
