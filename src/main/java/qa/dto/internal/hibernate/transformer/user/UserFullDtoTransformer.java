package qa.dto.internal.hibernate.transformer.user;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.user.UserAnswerDto;
import qa.dto.internal.hibernate.user.UserFullDto;
import qa.dto.internal.hibernate.user.UserQuestionDto;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//SQL
public class UserFullDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -5293402281360788622L;

    private final Map<Long, UserFullDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        Long id = ((BigInteger) objects[aliasToIndexMap.get(UserFullDto.ID)]).longValue();
        UserFullDto dto = dtoMap.computeIfAbsent(id, i -> new UserFullDto(objects, aliasToIndexMap));

        if (objects[aliasToIndexMap.get(UserAnswerDto.ID)] != null)
            dto.addQuestionIfAbsent(new UserQuestionDto(objects, aliasToIndexMap));
        if (objects[aliasToIndexMap.get(UserQuestionDto.ID)] != null)
            dto.addAnswerIfAbsent(new UserAnswerDto(objects, aliasToIndexMap));

        return dto;
    }

    @Override
    public List<UserFullDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
