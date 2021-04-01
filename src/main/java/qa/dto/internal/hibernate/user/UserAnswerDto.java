package qa.dto.internal.hibernate.user;

import java.math.BigInteger;
import java.util.Map;

public class UserAnswerDto {

    private final Long answerId;
    private final String text;

    public static final String ID = "u_a_id";
    public static final String TEXT = "u_a_text";

    public UserAnswerDto(Object[] tuples,
                         Map<String, Integer> aliasToIndexMap) {
        this.answerId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.text = (String) tuples[aliasToIndexMap.get(TEXT)];
    }

    public Long getAnswerId() {
        return answerId;
    }

    public String getText() {
        return text;
    }
}
