package qa.cache.like.remover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.CommentQuestionToLikeSetOperation;
import qa.cache.like.operation.UserCommentQuestionLikeSetOperation;

@Component
public class CommentQuestionCacheRemover extends CommentCacheRemover {

    @Autowired
    public CommentQuestionCacheRemover(UserCommentQuestionLikeSetOperation userCommentOperation,
                                       CommentQuestionToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }
}
