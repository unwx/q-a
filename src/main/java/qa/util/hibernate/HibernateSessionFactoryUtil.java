package qa.util.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.domain.setters.UserSetter;


public class HibernateSessionFactoryUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static final Logger logger = LogManager.getLogger(HibernateSessionFactoryUtil.class);

    private static SessionFactory buildSessionFactory() {
        try {
            return sessionFactory == null ? new Configuration().configure().buildSessionFactory() : sessionFactory;
        } catch (Throwable ex) {
            assert logger != null;
            logger.error("[hibernate sessionFactory initialization error]: " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
