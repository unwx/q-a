package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.domain.AuthenticationData;

import java.io.Serial;
import java.util.HashMap;

public class AuthenticationDataSetter implements DomainSetter<AuthenticationData> {

    private static final Logger logger = LogManager.getLogger(AuthenticationDataSetter.class);

    private AuthenticationDataSetter() {
    }

    private static AuthenticationDataSetter authenticationDataSetter;
    private static HashMap<String, ISetter<AuthenticationData>> setters;

    public static AuthenticationDataSetter getInstance() {
        if (authenticationDataSetter == null) {
            authenticationDataSetter = new AuthenticationDataSetter();
            try {
                initSetter();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                logger.error("[entity setter.class error]: error during initialization entity setters");
            }
        }
        return authenticationDataSetter;
    }

    private static void initSetter() throws Throwable {
        AuthenticationData e = new AuthenticationData();
        setters = new HashMap<>() {
            @Serial
            private static final long serialVersionUID = 3981351112247120013L;

            {
                SetterField[] fields = e.extractSettersField();
                for (SetterField field : fields) {
                    put(field.getName(), SetterFactory.getSetter(AuthenticationData.class, field.getName(), field.getType()));
                }
            }
        };
    }

    @Override
    public void setAll(AuthenticationData object, String[] names, Object[] values) {
        for (int i = 0; i < names.length; i++) {
            ISetter<AuthenticationData> s = setters.get(names[i]);
            s.set(object, values[i]);
        }
    }

    @Override
    public void set(AuthenticationData object, String name, Object value) {
        ISetter<AuthenticationData> s = setters.get(name);
        s.set(object, value);
    }
}
