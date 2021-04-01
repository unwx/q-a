package qa.dto.internal.hibernate.transformer;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.question.QuestionAnswerDto;
import qa.dto.internal.hibernate.question.QuestionCommentDto;
import qa.dto.internal.hibernate.question.QuestionFullDto;

import java.io.Serial;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuestionFullDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -1715850236897270316L;

    private final Map<String, QuestionFullDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        String questionTitle = (String) objects[aliasToIndexMap.get(QuestionFullDto.TITLE)];
        QuestionFullDto dto = dtoMap.computeIfAbsent(questionTitle, title -> new QuestionFullDto(objects, aliasToIndexMap));
        dto.getAnswers().add(new QuestionAnswerDto(objects, aliasToIndexMap));
        dto.getComments().add(new QuestionCommentDto(objects, aliasToIndexMap));

        return dto;
    }

    @Override
    public List transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
