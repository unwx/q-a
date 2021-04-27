package qa.cache.entity.like.provider.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.entity.like.provider.cache.CommentAnswerCacheProvider;
import qa.cache.operation.impl.CommentAnswerToLikeSetOperation;
import qa.cache.operation.impl.UserCommentAnswerLikeSetOperation;

@Component
public class CommentAnswerLikeProvider extends CommentLikeProvider {

    @Autowired
    public CommentAnswerLikeProvider(UserCommentAnswerLikeSetOperation userCommentOperation,
                                     CommentAnswerToLikeSetOperation commentOperation,
                                     CommentAnswerCacheProvider cacheProvider) {
        super(userCommentOperation, commentOperation, cacheProvider);
    }
}
