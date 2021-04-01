package qa.dto.internal.hibernate.question;

import java.util.Map;

public class QuestionAnswerAuthorDto {

    private final String username;

    public static final String C_AUTHOR_USERNAME = "q_a_a_username";

    public QuestionAnswerAuthorDto(Object[] tuples,
                           Map<String, Integer> aliasToIndexMap) {
        this.username = (String) tuples[aliasToIndexMap.get(C_AUTHOR_USERNAME)];
    }

    public String getUsername() {
        return username;
    }
}
