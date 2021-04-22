package qa.cache.operation;

import org.jetbrains.annotations.Nullable;
import qa.cache.size.QuestionToLikeSet;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionLikesOperation extends LikeSetOperationImpl<QuestionToLikeSet> implements HighLikeOperation<Long> {

    public QuestionLikesOperation(Jedis jedis) {
        super(jedis);
    }

    /**
     * after creating a question - the initial number of likes is set - 0
     */
    @Override
    public Long create(Long questionId) {
        QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.create(questionToLikeSet);
    }

    @Nullable
    @Override
    public Integer get(Long questionId) {
        QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.getK(questionToLikeSet);
    }

    @Override
    public List<Integer> get(List<Long> questionIds) {
        if (questionIds.isEmpty())
            return Collections.emptyList();
        List<QuestionToLikeSet> questionToLikeSets =
                questionIds
                        .stream()
                        .map(QuestionToLikeSet::new)
                        .collect(Collectors.toList());

        return super.getK(questionToLikeSets);
    }

    @Override
    public Long increment(Long questionId) {
        QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.increment(questionToLikeSet);
    }

    @Override
    public Long delete(Long questionId) {
        QuestionToLikeSet questionToLikeSet = new QuestionToLikeSet(questionId);
        return super.delete(questionToLikeSet);
    }
}
