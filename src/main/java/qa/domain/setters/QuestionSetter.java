package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.domain.Question;

import java.util.HashMap;

public class QuestionSetter implements DomainSetter<Question> {

    private final DomainSetterUtil<Question> domainSetterUtil = new DomainSetterUtil<>();

    private static final Logger logger = LogManager.getLogger(QuestionSetter.class);

    private QuestionSetter() {
    }

    private static QuestionSetter questionSetter;
    private static HashMap<String, ISetter<Question>> setters;

    public static QuestionSetter getInstance() {
        if (questionSetter == null) {
            questionSetter = new QuestionSetter();
            try {
                initSetters();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                logger.fatal("[entity setter.class error]: error during initialization entity setters");
            }
        }
        return questionSetter;
    }

    @Override
    public void setAll(Question object, String[] names, Object[] values) {
        domainSetterUtil.setAll(object, names, values, setters);
    }

    @Override
    public void set(Question object, String name, Object value) {
        domainSetterUtil.set(object, name, value, setters);
    }

    private static void initSetters() throws Throwable {
        setters = SettersInitializer.init(Question.class, new Question());
    }
}
