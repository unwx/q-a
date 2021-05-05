package qa.dto.internal.hibernate.transformer.question;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.entities.question.QuestionCommentDto;
import qa.dto.internal.hibernate.entities.question.QuestionWithCommentsDto;

import java.io.Serial;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//SQL
public class QuestionWithCommentsDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -5897667455291320140L;

    private final Map<String, QuestionWithCommentsDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        final Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        final String questionTitle = (String) objects[aliasToIndexMap.get(QuestionWithCommentsDto.TITLE)];
        final QuestionWithCommentsDto dto = dtoMap.computeIfAbsent(questionTitle, title -> new QuestionWithCommentsDto(objects, aliasToIndexMap));

        if (objects[aliasToIndexMap.get(QuestionCommentDto.ID)] != null)
            dto.getComments().add(new QuestionCommentDto(objects, aliasToIndexMap));

        return dto;
    }

    @Override
    public List<QuestionWithCommentsDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
