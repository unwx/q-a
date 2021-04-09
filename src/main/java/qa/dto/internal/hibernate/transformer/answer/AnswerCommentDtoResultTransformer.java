package qa.dto.internal.hibernate.transformer.answer;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.answer.AnswerCommentDto;
import qa.exceptions.dao.NullResultException;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//SQL
public class AnswerCommentDtoResultTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = 8679690115465009038L;

    private final Map<Long, AnswerCommentDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        if (objects[aliasToIndexMap.get(AnswerCommentDto.ID)] == null)
            throw new NullResultException("comments not exist");

        Long commentId = ((BigInteger) objects[aliasToIndexMap.get(AnswerCommentDto.ID)]).longValue();
        return dtoMap.computeIfAbsent(commentId, id -> new AnswerCommentDto(objects, aliasToIndexMap));
    }

    @Override
    public List<AnswerCommentDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}