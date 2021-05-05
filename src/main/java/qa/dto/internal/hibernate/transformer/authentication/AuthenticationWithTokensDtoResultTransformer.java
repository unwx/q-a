package qa.dto.internal.hibernate.transformer.authentication;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.entities.authentication.AuthenticationWithTokensDto;

import java.io.Serial;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AuthenticationWithTokensDtoResultTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -2395408415581487983L;

    private final Map<Long, AuthenticationWithTokensDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        final Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);
        final long id = ((BigInteger) objects[aliasToIndexMap.get(AuthenticationWithTokensDto.ID)]).longValue();

        return dtoMap.computeIfAbsent(id, i -> new AuthenticationWithTokensDto(objects, aliasToIndexMap));
    }

    @Override
    public List<AuthenticationWithTokensDto> transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
