package qa.util.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateSessionFactoryUtil {

    private static SessionFactory sessionFactory;

    private static final Logger logger = LogManager.getLogger(HibernateSessionFactoryUtil.class);

    private HibernateSessionFactoryUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            if (sessionFactory == null) {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            }
        } catch (Throwable ex) {
            assert logger != null;
            logger.error("[hibernate sessionFactory initialization error]: " + ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        return buildSessionFactory();
    }
}
