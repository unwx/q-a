package qa.dto.internal.hibernate.question;

import java.util.Map;

public class QuestionAnswerCommentAuthorDto {

    private final String username;

    public static final String USERNAME = "q_a_c_a_username";

    public QuestionAnswerCommentAuthorDto(Object[] tuples,
                                          Map<String, Integer> aliasToIndexMap) {
        this.username = (String) tuples[aliasToIndexMap.get(USERNAME)];
    }

    public String getUsername() {
        return username;
    }
}
