package qa.dto.internal.hibernate.transformer;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.user.UserQuestionDto;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//SQL
public class UserQuestionDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -5248252702469100541L;

    private final Map<Long, UserQuestionDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        Long id = ((BigInteger) objects[aliasToIndexMap.get(UserQuestionDto.ID)]).longValue();
        return dtoMap.computeIfAbsent(id, i -> new UserQuestionDto(objects, aliasToIndexMap));
    }

    @Override
    public List<UserQuestionDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
