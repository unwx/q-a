package qa.util.dao.query.params;

import java.util.Date;

public final class CommentQueryParameters {

    private CommentQueryParameters(){
    }

    public static final String TEXT = "thank you! @username";
    public static final Date DATE = new Date(99999999999999L);
    public static final Long QUESTION_ID = 1L;
    public static final Long ANSWER_ID = 1L;
}
