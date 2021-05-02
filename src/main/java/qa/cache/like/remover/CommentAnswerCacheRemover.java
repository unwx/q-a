package qa.cache.like.remover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.impl.CommentAnswerToLikeSetOperation;
import qa.cache.like.operation.impl.UserCommentAnswerLikeSetOperation;

@Component
public class CommentAnswerCacheRemover extends CommentCacheRemover {

    @Autowired
    public CommentAnswerCacheRemover(UserCommentAnswerLikeSetOperation userCommentOperation,
                                     CommentAnswerToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }
}
