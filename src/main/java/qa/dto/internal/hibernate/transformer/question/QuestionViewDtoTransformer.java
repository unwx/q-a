package qa.dto.internal.hibernate.transformer.question;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.entities.question.QuestionViewDto;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//SQL
public class QuestionViewDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = 5079239169839033814L;

    private final Map<Long, QuestionViewDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        final Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        final Long id = ((BigInteger) objects[aliasToIndexMap.get(QuestionViewDto.ID)]).longValue();
        return dtoMap.computeIfAbsent(id, i -> new QuestionViewDto(objects, aliasToIndexMap));
    }

    @Override
    public List<QuestionViewDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
