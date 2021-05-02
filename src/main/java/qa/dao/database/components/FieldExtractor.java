package qa.dao.database.components;

import qa.dao.Domain;

public interface FieldExtractor extends Domain {
    Field[] extract();
}
