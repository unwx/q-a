package qa.dto.internal.hibernate.transformer.question;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.question.QuestionFullStringIdsDto;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//SQL
public class QuestionFullIdsDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -2033093304654394979L;

    private final Map<Long, QuestionFullStringIdsDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        final Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);
        final QuestionFullStringIdsDto dto = dtoMap.computeIfAbsent(0L, none -> new QuestionFullStringIdsDto());

        if (objects[aliasToIndexMap.get(QuestionFullStringIdsDto.ANSWER_ID)] != null)
            dto.addAnswerIdsNX((BigInteger) objects[aliasToIndexMap.get(QuestionFullStringIdsDto.ANSWER_ID)]);

        if (objects[aliasToIndexMap.get(QuestionFullStringIdsDto.COMMENT_QUESTION_ID)] != null)
            dto.addCommentQuestionIdsNX((BigInteger) objects[aliasToIndexMap.get(QuestionFullStringIdsDto.COMMENT_QUESTION_ID)]);

        if (objects[aliasToIndexMap.get(QuestionFullStringIdsDto.COMMENT_ANSWER_ID)] != null)
            dto.addCommentAnswerIdsNX((BigInteger) objects[aliasToIndexMap.get(QuestionFullStringIdsDto.COMMENT_ANSWER_ID)]);

        return dto;
    }

    @Override
    public List<QuestionFullStringIdsDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
