package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.domain.User;

import java.io.Serial;
import java.util.HashMap;

public class UserSetter implements DomainSetter<User> {

    private static final Logger logger = LogManager.getLogger(UserSetter.class);

    private UserSetter() {
    }

    private static UserSetter userSetter;
    private static HashMap<String, ISetter<User>> setters;

    public static UserSetter getInstance() {
        if (userSetter == null) {
            userSetter = new UserSetter();
            try {
                initSetter();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                logger.error("[entity setter.class error]: error during initialization entity setters");
            }
        }
        return userSetter;
    }

    private static void initSetter() throws Throwable {
        User e = new User();
        setters = new HashMap<>() {

            @Serial
            private static final long serialVersionUID = 600318623424078250L;

            {
                SetterField[] fields = e.extractSettersField();
                for (SetterField field : fields) {
                    put(field.getName(), SetterFactory.getSetter(User.class, field.getName(), field.getType()));
                }
            }
        };
    }

    @Override
    public void setAll(User object, String[] names, Object[] values) {
        for (int i = 0; i < names.length; i++) {
            ISetter<User> s = setters.get(names[i]);
            s.set(object, values[i]);
        }
    }

    @Override
    public void set(User object, String name, Object value) {
        ISetter<User> s = setters.get(name);
        s.set(object, value);
    }
}
