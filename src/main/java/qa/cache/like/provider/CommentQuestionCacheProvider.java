package qa.cache.like.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.cache.like.operation.CommentQuestionToLikeSetOperation;
import qa.cache.like.operation.UserCommentQuestionLikeSetOperation;

@Component
public class CommentQuestionCacheProvider extends CommentCacheProvider {

    @Autowired
    public CommentQuestionCacheProvider(UserCommentQuestionLikeSetOperation userCommentOperation,
                                        CommentQuestionToLikeSetOperation commentOperation) {
        super(userCommentOperation, commentOperation);
    }
}
