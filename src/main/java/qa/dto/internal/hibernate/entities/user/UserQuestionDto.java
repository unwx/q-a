package qa.dto.internal.hibernate.entities.user;

import java.math.BigInteger;
import java.util.Map;

public class UserQuestionDto {

    private final Long questionId;
    private final String title;

    public static final String ID = "usr_q_id";
    public static final String TITLE = "usr_q_title";

    public UserQuestionDto(Object[] tuples,
                           Map<String, Integer> aliasToIndexMap) {
        this.questionId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.title = (String) tuples[aliasToIndexMap.get(TITLE)];
    }

    public Long getQuestionId() {
        return questionId;
    }

    public String getTitle() {
        return title;
    }
}
