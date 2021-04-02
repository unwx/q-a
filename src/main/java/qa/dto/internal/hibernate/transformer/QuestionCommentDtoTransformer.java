package qa.dto.internal.hibernate.transformer;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.question.QuestionCommentDto;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// SQL
public class QuestionCommentDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = 5116983997523335962L;

    private final Map<Long, QuestionCommentDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        Long id = ((BigInteger) objects[aliasToIndexMap.get(QuestionCommentDto.ID)]).longValue();
        return dtoMap.computeIfAbsent(id, i -> new QuestionCommentDto(objects, aliasToIndexMap));
    }

    @Override
    public List<QuestionCommentDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
