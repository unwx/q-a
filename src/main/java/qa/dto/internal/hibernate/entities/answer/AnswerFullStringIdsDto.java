package qa.dto.internal.hibernate.entities.answer;

import java.math.BigInteger;
import java.util.Stack;

public class AnswerFullStringIdsDto {

    private final Stack<String> commentAnswerIds = new Stack<>();

    public static final String ID = "com_ans_id";

    public void addCommentAnswer(BigInteger id) {
        commentAnswerIds.push(id.toString());
    }

    public Stack<String> getCommentAnswerIds() {
        return commentAnswerIds;
    }
}
