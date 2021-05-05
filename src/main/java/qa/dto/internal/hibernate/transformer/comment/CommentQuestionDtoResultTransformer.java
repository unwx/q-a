package qa.dto.internal.hibernate.transformer.comment;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.entities.comment.question.CommentQuestionDto;
import qa.exceptions.dao.NullResultException;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommentQuestionDtoResultTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -2973791520355126193L;

    private final Map<Long, CommentQuestionDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        final Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        if (objects[aliasToIndexMap.get(CommentQuestionDto.ID)] == null)
            throw new NullResultException("comments not exist");

        final long commentId = ((BigInteger) objects[aliasToIndexMap.get(CommentQuestionDto.ID)]).longValue();
        return dtoMap.computeIfAbsent(commentId, id -> new CommentQuestionDto(objects, aliasToIndexMap));
    }

    @Override
    public List<CommentQuestionDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
