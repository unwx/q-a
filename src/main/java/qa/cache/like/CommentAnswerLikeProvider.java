package qa.cache.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.impl.CommentAnswerToLikeSetOperation;
import qa.cache.like.operation.impl.UserCommentAnswerLikeSetOperation;
import qa.cache.like.provider.CommentAnswerCacheProvider;
import qa.cache.like.remover.CommentAnswerCacheRemover;

@Component
public class CommentAnswerLikeProvider extends CommentLikeProvider {

    @Autowired
    public CommentAnswerLikeProvider(UserCommentAnswerLikeSetOperation userCommentOperation,
                                     CommentAnswerToLikeSetOperation commentOperation,
                                     CommentAnswerCacheProvider cacheProvider,
                                     CommentAnswerCacheRemover cacheRemover) {
        super(userCommentOperation, commentOperation, cacheProvider, cacheRemover);
    }
}
