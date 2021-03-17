package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.domain.User;

import java.util.HashMap;

public class UserSetter implements DomainSetter<User> {

    private final DomainSetterUtil<User> domainSetterUtil = new DomainSetterUtil<>();

    private static final Logger logger = LogManager.getLogger(UserSetter.class);

    private UserSetter() {
    }

    private static UserSetter userSetter;
    private static HashMap<String, ISetter<User>> setters;

    public static UserSetter getInstance() {
        if (userSetter == null) {
            userSetter = new UserSetter();
            try {
                initSetters();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                logger.fatal("[entity setter.class error]: error during initialization entity setters");
            }
        }
        return userSetter;
    }

    private static void initSetters() throws Throwable {
        setters = SettersInitializer.init(User.class, new User());
    }

    @Override
    public void setAll(User object, String[] names, Object[] values) {
        domainSetterUtil.setAll(object, names, values, setters);
    }

    @Override
    public void set(User object, String name, Object value) {
        domainSetterUtil.set(object, name, value, setters);
    }
}
