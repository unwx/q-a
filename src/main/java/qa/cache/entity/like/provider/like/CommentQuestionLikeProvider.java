package qa.cache.entity.like.provider.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.entity.like.provider.cache.CommentQuestionCacheProvider;
import qa.cache.entity.like.provider.remover.CommentQuestionCacheRemover;
import qa.cache.operation.impl.CommentQuestionToLikeSetOperation;
import qa.cache.operation.impl.UserCommentQuestionLikeSetOperation;

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
