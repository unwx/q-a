package qa.domain.setters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import qa.domain.Comment;

import java.util.HashMap;

public class CommentSetter implements DomainSetter<Comment> {

    private final DomainSetterUtil<Comment> domainSetterUtil = new DomainSetterUtil<>();

    private static final Logger logger = LogManager.getLogger(CommentSetter.class);

    private CommentSetter() {
    }

    private static CommentSetter commentSetter;
    private static HashMap<String, ISetter<Comment>> setters;

    public static CommentSetter getInstance() {
        if (commentSetter == null) {
            commentSetter = new CommentSetter();
            try {
                initSetters();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                logger.fatal("[entity setter.class error]: error during initialization entity setters");
            }
        }
        return commentSetter;
    }

    @Override
    public void setAll(Comment object, String[] names, Object[] values) {
        domainSetterUtil.setAll(object, names, values, setters);
    }

    @Override
    public void set(Comment object, String name, Object value) {
        domainSetterUtil.set(object, name, value, setters);
    }

    private static void initSetters() throws Throwable {
        setters = SettersInitializer.init(Comment.class, new Comment());
    }
}
