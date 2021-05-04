package qa.service.util;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.Dao;
import qa.dao.database.components.Where;
import qa.exceptions.rest.AccessDeniedException;
import qa.exceptions.rest.ResourceNotFoundException;
import qa.exceptions.service.internal.AuthorNotExistException;

@Component
public class AuthorUtil {

    private final ActionAccessUtil accessUtil;

    private static final String ERR_ENTITY_NOT_EXIST    = "%s not exist. id: %s";
    private static final String ERR_AUTHOR_NOT_EXIST    = "author not exist";
    private static final String ERR_PERMISSION          = "you do not have permission to this %s";

    @Autowired
    public AuthorUtil(ActionAccessUtil accessUtil) {
        this.accessUtil = accessUtil;
    }

    public <E extends HasAuthor> void checkRightsAndExistence(Long authenticationId,
                                                                               Where where,
                                                                               E entity,
                                                                               Dao<E, ?> dao,
                                                                               Logger logger,
                                                                               String entityName) {

        final CheckAuthorResult result = this.accessUtil.isRealAuthor(where, authenticationId, entity, dao);
        switch (result) {
            case ENTITY_NOT_EXIST -> throw new ResourceNotFoundException(ERR_ENTITY_NOT_EXIST.formatted(entityName, where.getFieldValue()));

            case AUTHOR_NOT_EXIST -> {
                final String message = ERR_AUTHOR_NOT_EXIST;

                logger.error(message);
                throw new AuthorNotExistException(message);
            }

            case NOT_REAL_AUTHOR -> throw new AccessDeniedException(ERR_PERMISSION.formatted(entityName));
        }
    }
}
