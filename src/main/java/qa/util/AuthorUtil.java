package qa.util;
import org.apache.logging.log4j.Logger;
import qa.dao.Dao;
import qa.dao.databasecomponents.Where;
import qa.domain.setters.PropertySetterFactory;
import qa.exceptions.rest.AccessDeniedException;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.service.internal.AuthorNotExistException;
import qa.util.access.AccessUtil;
import qa.util.access.CheckAuthorResult;
import qa.util.access.HasAuthor;

public class AuthorUtil {

    private AuthorUtil() {

    }

    public static <E extends HasAuthor> void checkIsRealAuthor(Long authenticationId,
                                                               Where where,
                                                               Class<E> clazz,
                                                               Dao<E, ?> dao,
                                                               PropertySetterFactory propertySetterFactory,
                                                               Logger logger) {

        CheckAuthorResult result = AccessUtil.isRealAuthor(where, authenticationId, clazz, dao, propertySetterFactory);
        switch (result) {
            case ENTITY_NOT_EXIST -> throw new BadRequestException(getEntityName(clazz) + " not exist. id: " + where.getFieldValue());
            case AUTHOR_NOT_EXIST -> {
                String message = getEntityName(clazz) + where.getFieldValue() + ". Author not exist";
                logger.error(message);
                throw new AuthorNotExistException(message);
            }
            case NOT_REAL_AUTHOR -> throw new AccessDeniedException("you do not have permission to this " + getEntityName(clazz));
        }
    }

    private static String getEntityName(Class<?> clazz) {
        String clazzName = clazz.getSimpleName();
        return Character.toUpperCase(clazzName.charAt(0)) + clazzName.substring(1);
    }
}
