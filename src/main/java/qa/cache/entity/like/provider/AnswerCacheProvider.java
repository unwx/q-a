package qa.cache.entity.like.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.CacheProvider;
import qa.cache.operation.impl.*;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.DomainName;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.List;

@Component
public class AnswerCacheProvider extends CacheProvider {

    @Autowired
    public AnswerCacheProvider(QuestionToLikeSetOperation questionLikeOperation,
                                  AnswerToLikeSetOperation answerLikeOperation,
                                  CommentQuestionToLikeSetOperation commentQuestionLikeOperation,
                                  CommentAnswerToLikeSetOperation commentAnswerLikeOperation,
                                  UserQuestionLikeSetOperation userQuestionLikeOperation,
                                  UserAnswerLikeSetOperation userAnswerLikeOperation,
                                  UserCommentQuestionLikeSetOperation userCommentQuestionLikeOperation,
                                  UserCommentAnswerLikeSetOperation userCommentAnswerLikeOperation) {
        super(
                questionLikeOperation,
                answerLikeOperation,
                commentQuestionLikeOperation,
                commentAnswerLikeOperation,
                userQuestionLikeOperation,
                userAnswerLikeOperation,
                userCommentQuestionLikeOperation,
                userCommentAnswerLikeOperation
        );
    }

    public void provide(List<Answer> answers, long userId, Jedis jedis) {
        final String userIdStr = String.valueOf(userId);

        final List<CommentAnswer> comments = new LinkedList<>();
        answers.forEach((a) -> comments.addAll(a.getComments()));

        super.provide(answers, userIdStr, DomainName.ANSWER, jedis);
        super.provide(comments, userIdStr, DomainName.COMMENT_ANSWER, jedis);
    }
}
