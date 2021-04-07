package qa.util.dao.query.builder;

import org.hibernate.Session;

public interface SessionInitializer {
    SessionInitializer with(Session session);
}
