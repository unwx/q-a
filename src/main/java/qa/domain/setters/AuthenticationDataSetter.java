package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.domain.AuthenticationData;

import java.util.HashMap;

public class AuthenticationDataSetter implements DomainSetter<AuthenticationData> {

    private final DomainSetterUtil<AuthenticationData> domainSetterUtil = new DomainSetterUtil<>();

    private static final Logger logger = LogManager.getLogger(AuthenticationDataSetter.class);

    private AuthenticationDataSetter() {
    }

    private static AuthenticationDataSetter authenticationDataSetter;
    private static HashMap<String, ISetter<AuthenticationData>> setters;

    public static AuthenticationDataSetter getInstance() {
        if (authenticationDataSetter == null) {
            authenticationDataSetter = new AuthenticationDataSetter();
            try {
                initSetters();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                logger.fatal("[entity setter.class error]: error during initialization entity setters");
            }
        }
        return authenticationDataSetter;
    }

    private static void initSetters() throws Throwable {
        setters = SettersInitializer.init(AuthenticationData.class, new AuthenticationData());
    }

    @Override
    public void setAll(AuthenticationData object, String[] names, Object[] values) {
        domainSetterUtil.setAll(object, names, values, setters);
    }

    @Override
    public void set(AuthenticationData object, String name, Object value) {
        domainSetterUtil.set(object, name, value, setters);
    }
}
