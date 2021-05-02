package qa.cache.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.impl.CommentQuestionToLikeSetOperation;
import qa.cache.like.operation.impl.UserCommentQuestionLikeSetOperation;
import qa.cache.like.provider.CommentQuestionCacheProvider;
import qa.cache.like.remover.CommentQuestionCacheRemover;

@Component
public class CommentQuestionLikeProvider extends CommentLikeProvider {

    @Autowired
    public CommentQuestionLikeProvider(UserCommentQuestionLikeSetOperation userCommentOperation,
                                       CommentQuestionToLikeSetOperation commentOperation,
                                       CommentQuestionCacheProvider cacheProvider,
                                       CommentQuestionCacheRemover cacheRemover) {
        super(userCommentOperation, commentOperation, cacheProvider, cacheRemover);
    }
}
