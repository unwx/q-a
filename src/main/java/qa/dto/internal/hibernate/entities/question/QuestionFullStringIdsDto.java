package qa.dto.internal.hibernate.entities.question;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Stack;

public class QuestionFullStringIdsDto {

    private final Stack<String> answerIds = new Stack<>();
    private final Stack<String> commentQuestionIds = new Stack<>();
    private final Stack<String> commentAnswerIds = new Stack<>();

    private final HashSet<BigInteger> answerIdsSet = new HashSet<>();
    private final HashSet<BigInteger> commentQuestionIdsSet = new HashSet<>();
    private final HashSet<BigInteger> commentAnswerIdsSet = new HashSet<>();

    public static final String ANSWER_ID                = "ans_id";
    public static final String COMMENT_QUESTION_ID      = "com_que_id";
    public static final String COMMENT_ANSWER_ID        = "com_ans_id";

    public void addAnswerIdsNX(BigInteger id) {
        if (!answerIdsSet.contains(id)) {
            answerIds.push(id.toString());
            answerIdsSet.add(id);
        }
    }

    public void addCommentQuestionIdsNX(BigInteger id) {
        if (!commentQuestionIdsSet.contains(id)) {
            commentQuestionIds.push(id.toString());
            commentQuestionIdsSet.add(id);
        }
    }

    public void addCommentAnswerIdsNX(BigInteger id) {
        if (!commentAnswerIdsSet.contains(id)) {
            commentAnswerIds.push(id.toString());
            commentAnswerIdsSet.add(id);
        }
    }

    public Stack<String> getAnswerIds() {
        return answerIds;
    }

    public Stack<String> getCommentQuestionIds() {
        return commentQuestionIds;
    }

    public Stack<String> getCommentAnswerIds() {
        return commentAnswerIds;
    }
}
