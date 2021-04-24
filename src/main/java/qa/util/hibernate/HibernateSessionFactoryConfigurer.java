package qa.util.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateSessionFactoryConfigurer {

    private static SessionFactory sessionFactory;
    private static final String ERR_INITIALIZATION = "hibernate sessionFactory initialization error: %s";

    private static final Logger logger = LogManager.getLogger(HibernateSessionFactoryConfigurer.class);

    private HibernateSessionFactoryConfigurer() {}

    private static SessionFactory buildSessionFactory() {
        try {
            if (sessionFactory == null) {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            }
        } catch (Throwable ex) {
            logger.error(ERR_INITIALIZATION.formatted(ex.getMessage()));
            throw new ExceptionInInitializerError(ex);
        }
        return sessionFactory;
    }

    public static SessionFactory getSessionFactory() {
        return buildSessionFactory();
    }
}
