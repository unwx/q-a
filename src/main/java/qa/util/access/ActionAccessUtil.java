package qa.util.access;

import qa.dao.Dao;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;

import java.util.Collections;

public final class ActionAccessUtil {

    private ActionAccessUtil() {
    }

    public static <E extends HasAuthor> CheckAuthorResult isRealAuthor(Where where,
                                                                       Long authenticationId,
                                                                       Class<E> clazz,
                                                                       Dao<E, ?> dao,
                                                                       PropertySetterFactory propertySetterFactory) {
        E e = dao.read(
                where,
                new Table(new String[]{}, clazz.getSimpleName()),
                Collections.singletonList(
                        new NestedEntity(new String[]{"id"},
                                User.class,
                                "author",
                                propertySetterFactory.getSetter(new User()))));

        if (e == null)
            return CheckAuthorResult.ENTITY_NOT_EXIST;

        if (e.getAuthor() == null)
            return CheckAuthorResult.AUTHOR_NOT_EXIST;

        if (!e.getAuthor().getId().equals(authenticationId))
            return CheckAuthorResult.NOT_REAL_AUTHOR;

        return CheckAuthorResult.OK;
    }
}
