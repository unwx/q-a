package qa.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.Dao;
import qa.dao.database.components.NestedEntity;
import qa.dao.database.components.Table;
import qa.dao.database.components.Where;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;

import java.util.Collections;

@Component
public final class ActionAccessUtil {

    private final PropertySetterFactory propertySetterFactory;

    @Autowired
    public ActionAccessUtil(PropertySetterFactory propertySetterFactory) {
        this.propertySetterFactory = propertySetterFactory;
    }

    public <E extends HasAuthor> CheckAuthorResult isRealAuthor(Where where,
                                                                       Long authenticationId,
                                                                       E entity,
                                                                       Dao<E, ?> dao) {
        final NestedEntity nested = new NestedEntity(
                new String[]{"id"},
                User.class,
                "author",
                propertySetterFactory.getSetter(new User())
        );
        final Table table = new Table(new String[]{}, entity.getClassName());
        final E result = dao.read(where, table, Collections.singletonList(nested));

        if (result == null)
            return CheckAuthorResult.ENTITY_NOT_EXIST;

        if (result.getAuthor() == null)
            return CheckAuthorResult.AUTHOR_NOT_EXIST;

        if (!result.getAuthor().getId().equals(authenticationId))
            return CheckAuthorResult.NOT_REAL_AUTHOR;

        return CheckAuthorResult.OK;
    }
}
