package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.domain.Answer;

import java.util.HashMap;

public class AnswerSetter implements DomainSetter<Answer> {

    private final DomainSetterUtil<Answer> domainSetterUtil = new DomainSetterUtil<>();

    private static final Logger logger = LogManager.getLogger(AnswerSetter.class);

    private AnswerSetter(){

    }

    private static AnswerSetter answerSetter;
    private static HashMap<String, ISetter<Answer>> setters;

    public static AnswerSetter getInstance() {
        if (answerSetter == null) {
            answerSetter = new AnswerSetter();
            try {
                initSetters();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                logger.fatal("[entity setter.class error]: error during initialization entity setters");
            }
        }
        return answerSetter;
    }

    @Override
    public void setAll(Answer object, String[] names, Object[] values) {
        domainSetterUtil.setAll(object, names, values, setters);
    }

    @Override
    public void set(Answer object, String name, Object value) {
        domainSetterUtil.set(object, name, value, setters);
    }

    private static void initSetters() throws Throwable {
        setters = SettersInitializer.init(Answer.class, new Answer());
    }
}
