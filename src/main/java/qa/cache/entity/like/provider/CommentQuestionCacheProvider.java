package qa.cache.entity.like.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.CacheProvider;
import qa.cache.operation.impl.*;
import qa.domain.CommentQuestion;
import qa.domain.DomainName;
import redis.clients.jedis.Jedis;

import java.util.List;

@Component
public class CommentQuestionCacheProvider extends CacheProvider {

    @Autowired
    public CommentQuestionCacheProvider(QuestionToLikeSetOperation questionLikeOperation,
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

    public void provide(List<CommentQuestion> comments, long userId, Jedis jedis) {
        final String userIdStr = String.valueOf(userId);
        super.provide(comments, userIdStr, DomainName.COMMENT_QUESTION, jedis);
    }
}
