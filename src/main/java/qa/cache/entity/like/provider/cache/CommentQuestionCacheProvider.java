package qa.cache.entity.like.provider.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.operation.impl.CommentQuestionToLikeSetOperation;
import qa.cache.operation.impl.UserCommentQuestionLikeSetOperation;

@Component
public class CommentQuestionCacheProvider extends CommentCacheProvider {

    @Autowired
    public CommentQuestionCacheProvider(UserCommentQuestionLikeSetOperation userCommentOperation,
                                        CommentQuestionToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }
}
