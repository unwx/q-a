package qa.dto.internal.hibernate.transformer.user;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.user.UserAnswerDto;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//SQL
public class UserAnswerDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -952721200779646961L;

    private final Map<Long, UserAnswerDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        Long id = ((BigInteger) objects[aliasToIndexMap.get(UserAnswerDto.ID)]).longValue();
        return dtoMap.computeIfAbsent(id, i -> new UserAnswerDto(objects, aliasToIndexMap));
    }

    @Override
    public List<UserAnswerDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
