package qa.dto.internal.hibernate.transformer.comment;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.comment.answer.CommentAnswerDto;
import qa.exceptions.dao.NullResultException;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//SQL
public class CommentAnswerDtoResultTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -1844463932419217970L;

    private final Map<Long, CommentAnswerDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        final Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        if (objects[aliasToIndexMap.get(CommentAnswerDto.ID)] == null)
            throw new NullResultException("comments not exist");

        final long commentId = ((BigInteger) objects[aliasToIndexMap.get(CommentAnswerDto.ID)]).longValue();
        return dtoMap.computeIfAbsent(commentId, id -> new CommentAnswerDto(objects, aliasToIndexMap));
    }

    @Override
    public List<CommentAnswerDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
