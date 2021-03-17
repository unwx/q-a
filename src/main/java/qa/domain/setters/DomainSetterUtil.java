package qa.domain.setters;

import qa.dao.databasecomponents.FieldDataSetterExtractor;

import java.util.HashMap;

public class DomainSetterUtil<T extends FieldDataSetterExtractor> {

    public void setAll(T object, String[] names, Object[] values, HashMap<String, ISetter<T>> setters) {
        for (int i = 0; i < names.length; i++) {
            ISetter<T> s = setters.get(names[i]);
            s.set(object, values[i]);
        }
    }

    public void set(T object, String name, Object value, HashMap<String, ISetter<T>> setters) {
        ISetter<T> s = setters.get(name);
        s.set(object, value);
    }
}
