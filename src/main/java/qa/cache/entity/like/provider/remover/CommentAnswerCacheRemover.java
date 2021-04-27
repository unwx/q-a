package qa.cache.entity.like.provider.remover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.operation.impl.CommentAnswerToLikeSetOperation;
import qa.cache.operation.impl.UserCommentAnswerLikeSetOperation;

@Component
public class CommentAnswerCacheRemover extends CommentCacheRemover {

    @Autowired
    public CommentAnswerCacheRemover(UserCommentAnswerLikeSetOperation userCommentOperation,
                                     CommentAnswerToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }
}
