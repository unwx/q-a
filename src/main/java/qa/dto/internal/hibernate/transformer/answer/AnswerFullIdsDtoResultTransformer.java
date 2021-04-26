package qa.dto.internal.hibernate.transformer.answer;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.answer.AnswerFullStringIdsDto;
import qa.dto.internal.hibernate.question.QuestionFullStringIdsDto;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnswerFullIdsDtoResultTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = 3241401637870590529L;

    private final Map<Long, AnswerFullStringIdsDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        final Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);
        final AnswerFullStringIdsDto dto = dtoMap.computeIfAbsent(0L, none -> new AnswerFullStringIdsDto());

        if (objects[aliasToIndexMap.get(QuestionFullStringIdsDto.COMMENT_ANSWER_ID)] != null)
            dto.addCommentAnswer((BigInteger) objects[aliasToIndexMap.get(QuestionFullStringIdsDto.COMMENT_ANSWER_ID)]);

        return dto;
    }

    @Override
    public List<AnswerFullStringIdsDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
