package qa.cache.entity.like.provider.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.operation.impl.CommentAnswerToLikeSetOperation;
import qa.cache.operation.impl.UserCommentAnswerLikeSetOperation;

@Component
public class CommentAnswerCacheProvider extends CommentCacheProvider {

    @Autowired
    public CommentAnswerCacheProvider(UserCommentAnswerLikeSetOperation userCommentOperation,
                                      CommentAnswerToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }
}
