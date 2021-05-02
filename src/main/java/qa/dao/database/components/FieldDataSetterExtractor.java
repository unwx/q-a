package qa.dao.database.components;

import qa.dao.Domain;
import qa.domain.setters.SetterField;

public interface FieldDataSetterExtractor extends Domain {
    SetterField[] extractSettersField();
}
