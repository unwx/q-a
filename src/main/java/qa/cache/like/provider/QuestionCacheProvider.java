package qa.cache.like.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.impl.QuestionToLikeSetOperation;
import qa.cache.like.operation.impl.UserQuestionLikeSetOperation;
import qa.domain.*;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.List;

@Component
public class QuestionCacheProvider extends CacheProvider {

    private final UserQuestionLikeSetOperation userQuestionOperation;
    private final QuestionToLikeSetOperation questionOperation;

    private final AnswerCacheProvider answerCacheProvider;
    private final CommentQuestionCacheProvider commentQuestionCacheProvider;
    private final CommentAnswerCacheProvider commentAnswerCacheProvider;

    @Autowired
    public QuestionCacheProvider(UserQuestionLikeSetOperation userQuestionOperation,
                                 QuestionToLikeSetOperation questionOperation,
                                 AnswerCacheProvider answerCacheProvider,
                                 CommentQuestionCacheProvider commentQuestionCacheProvider,
                                 CommentAnswerCacheProvider commentAnswerCacheProvider) {
        this.userQuestionOperation = userQuestionOperation;
        this.questionOperation = questionOperation;
        this.answerCacheProvider = answerCacheProvider;
        this.commentQuestionCacheProvider = commentQuestionCacheProvider;
        this.commentAnswerCacheProvider = commentAnswerCacheProvider;
    }

    public void provide(Question question, long userId, Jedis jedis) {
        final String userIdStr = String.valueOf(userId);

        final List<Answer> answers = question.getAnswers();
        final List<CommentQuestion> commentQuestions = question.getComments();
        final List<CommentAnswer> commentAnswers = new LinkedList<>();
        answers.forEach((a) -> commentAnswers.addAll(a.getComments()));

        super.provide(question, userIdStr, userQuestionOperation, questionOperation, jedis);
        this.answerCacheProvider.provide(answers, userIdStr, jedis);
        this.commentQuestionCacheProvider.provide(commentQuestions, userIdStr, jedis);
        this.commentAnswerCacheProvider.provide(commentAnswers, userIdStr, jedis);
    }

    public void provide(List<QuestionView> views, Jedis jedis) {
        super.provide(views, questionOperation, jedis);
    }
}
