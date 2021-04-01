package qa.dto.internal.hibernate.transformer;

import org.hibernate.transform.ResultTransformer;
import qa.dto.internal.hibernate.AliasUtil;
import qa.dto.internal.hibernate.user.UserAnswerDto;
import qa.dto.internal.hibernate.user.UserFullDto;
import qa.dto.internal.hibernate.user.UserQuestionDto;

import java.io.Serial;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserFullDtoTransformer implements ResultTransformer {

    @Serial
    private static final long serialVersionUID = -5293402281360788622L;

    private final Map<String, UserFullDto> dtoMap = new LinkedHashMap<>();

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        Map<String, Integer> aliasToIndexMap = AliasUtil.aliasToIndexMap(strings);

        String about = (String) objects[aliasToIndexMap.get(UserFullDto.ABOUT)];
        UserFullDto dto = dtoMap.computeIfAbsent(about, a -> new UserFullDto(objects, aliasToIndexMap));
        dto.getAnswers().add(new UserAnswerDto(objects, aliasToIndexMap));
        dto.getQuestions().add(new UserQuestionDto(objects, aliasToIndexMap));

        return dto;
    }

    @Override
    public List transformList(List list) {
        return new ArrayList<>(dtoMap.values());
    }
}
