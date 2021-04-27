package qa.cache.entity.like.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.CacheProvider;
import qa.cache.operation.impl.*;
import qa.domain.*;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.List;

@Component
public class QuestionCacheProvider extends CacheProvider {

    @Autowired
    public QuestionCacheProvider(QuestionToLikeSetOperation questionLikeOperation,
                                 AnswerToLikeSetOperation answerLikeOperation,
                                 CommentQuestionToLikeSetOperation commentQuestionLikeOperation,
                                 CommentAnswerToLikeSetOperation commentAnswerLikeOperation,
                                 UserQuestionLikeSetOperation userQuestionLikeOperation,
                                 UserAnswerLikeSetOperation userAnswerLikeOperation,
                                 UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation,
                                 UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation) {
        super(questionLikeOperation,
                answerLikeOperation,
                commentQuestionLikeOperation,
                commentAnswerLikeOperation,
                userQuestionLikeOperation,
                userAnswerLikeOperation,
                userCommentQuestionLikeOperation,
                userCommentAnswerLikeOperation
        );
    }

    public void provide(Question question, long userId, Jedis jedis) {
        final String userIdStr = String.valueOf(userId);

        final List<Answer> answers = question.getAnswers();
        final List<CommentQuestion> commentQuestions = question.getComments();
        final List<CommentAnswer> commentAnswers = new LinkedList<>();
        answers.forEach((a) -> commentAnswers.addAll(a.getComments()));

        super.provide(question, userIdStr, DomainName.QUESTION, jedis);
        super.provide(answers, userIdStr, DomainName.ANSWER, jedis);
        super.provide(commentQuestions, userIdStr, DomainName.COMMENT_QUESTION, jedis);
        super.provide(commentAnswers, userIdStr, DomainName.COMMENT_ANSWER, jedis);
    }

    public void provide(List<QuestionView> views, Jedis jedis) {
        super.provide(views, DomainName.QUESTION, jedis);
    }
}
