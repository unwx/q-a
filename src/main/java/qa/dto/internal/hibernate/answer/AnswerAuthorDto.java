package qa.dto.internal.hibernate.answer;

import java.util.Map;

public class AnswerAuthorDto {

    private final String username;

    public static final String USERNAME = "ans_u_username";

    public AnswerAuthorDto(Object[] tuples,
                           Map<String, Integer> aliasToIndexMap) {
        this.username = (String) tuples[aliasToIndexMap.get(USERNAME)];
    }

    public String getUsername() {
        return username;
    }
}
