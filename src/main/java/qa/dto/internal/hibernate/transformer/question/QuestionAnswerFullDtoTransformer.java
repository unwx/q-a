package qa.dto.internal.hibernate.transformer.question;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.answer.AnswerCommentDto;
import qa.dto.internal.hibernate.answer.AnswerFullDto;
import qa.exceptions.dao.NullResultException;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//SQL
public class QuestionAnswerFullDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -8420707254816001382L;

    private final Map<Long, AnswerFullDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        final Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);
        if (objects[aliasToIndexMap.get(AnswerFullDto.ID)] == null)
            throw new NullResultException("answers not exist");

        final Long answerId = ((BigInteger) objects[aliasToIndexMap.get(AnswerFullDto.ID)]).longValue();
        final AnswerFullDto dto = dtoMap.computeIfAbsent(answerId, id -> new AnswerFullDto(objects, aliasToIndexMap));

        if (objects[aliasToIndexMap.get(AnswerCommentDto.ID)] != null)
            dtoMap.get(dto.getAnswerId()).getComments().add(new AnswerCommentDto(objects, aliasToIndexMap));

        return dto;
    }

    @Override
    public List<AnswerFullDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
