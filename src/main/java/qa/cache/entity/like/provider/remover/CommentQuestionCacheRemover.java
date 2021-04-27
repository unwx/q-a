package qa.cache.entity.like.provider.remover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.operation.impl.CommentQuestionToLikeSetOperation;
import qa.cache.operation.impl.UserCommentQuestionLikeSetOperation;

@Component
public class CommentQuestionCacheRemover extends CommentCacheRemover {

    @Autowired
    public CommentQuestionCacheRemover(UserCommentQuestionLikeSetOperation userCommentOperation,
                                       CommentQuestionToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }
}
