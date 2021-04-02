package qa.dto.internal.hibernate.transformer;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.answer.AnswerCommentDto;
import qa.dto.internal.hibernate.answer.AnswerFullDto;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuestionAnswerDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = 2601036050485006319L;

    private final Map<Long, AnswerFullDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        Long id = ((BigInteger) objects[aliasToIndexMap.get(AnswerFullDto.ID)]).longValue();
        AnswerFullDto dto = dtoMap.computeIfAbsent(id, i -> new AnswerFullDto(objects, aliasToIndexMap));
        dto.getComments().add(new AnswerCommentDto(objects, aliasToIndexMap));

        return dto;
    }

    @Override
    public List<AnswerFullDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
