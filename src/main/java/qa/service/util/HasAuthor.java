package qa.service.util;

import qa.dao.Domain;
import qa.domain.User;

public interface HasAuthor extends Domain {
    User getAuthor();
}
