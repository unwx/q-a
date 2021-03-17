package qa.domain.setters;

import qa.dao.databasecomponents.FieldDataSetterExtractor;

import java.io.Serial;
import java.util.HashMap;

public class SettersInitializer {

    public static <T extends FieldDataSetterExtractor> HashMap<String, ISetter<T>> init(Class<T> clazz, T obj) throws Throwable {
        return new HashMap<>() {

            @Serial
            private static final long serialVersionUID = 7124974512179832211L;

            {
                SetterField[] fields = obj.extractSettersField();
                for (SetterField field : fields) {
                    put(field.getName(), SetterFactory.getSetter(clazz, field.getName(), field.getType()));
                }
            }
        };
    }
}
