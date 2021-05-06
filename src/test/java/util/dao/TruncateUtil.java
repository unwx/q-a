package util.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import redis.clients.jedis.Jedis;

public class TruncateUtil {

    private TruncateUtil() {}

    public static void truncatePQ(Session session) {
        final Transaction transaction = session.beginTransaction();
        session.createSQLQuery("TRUNCATE TABLE authentication CASCADE").executeUpdate();
        session.createSQLQuery("TRUNCATE TABLE usr CASCADE").executeUpdate();
        session.createSQLQuery("TRUNCATE TABLE user_role CASCADE").executeUpdate();

        session.createSQLQuery("TRUNCATE TABLE question CASCADE").executeUpdate();
        session.createSQLQuery("TRUNCATE TABLE answer CASCADE").executeUpdate();
        session.createSQLQuery("TRUNCATE TABLE comment CASCADE").executeUpdate();
        transaction.commit();
    }

    public static void truncateRedis(Jedis jedis) {
        jedis.flushDB();
    }
}
