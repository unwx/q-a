package qa.config.beans;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;

@Configuration
public class DatabaseBeans {

    @Bean
    public SessionFactory sessionFactory() {
        return HibernateSessionFactoryConfigurer.getSessionFactory();
    }
}
