package qa.cache.like.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.CommentAnswerToLikeSetOperation;
import qa.cache.like.operation.UserCommentAnswerLikeSetOperation;

@Component
public class CommentAnswerCacheProvider extends CommentCacheProvider {

    @Autowired
    public CommentAnswerCacheProvider(UserCommentAnswerLikeSetOperation userCommentOperation,
                                      CommentAnswerToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }
}
