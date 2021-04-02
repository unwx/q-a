package qa.dto.internal.hibernate.question;

import java.util.Map;

public class QuestionCommentAuthorDto {

    private final String username;

    public static final String USERNAME = "que_c_u_username";

    public QuestionCommentAuthorDto(Object[] tuples,
                                    Map<String, Integer> aliasToIndexMap) {
        this.username = (String) tuples[aliasToIndexMap.get(USERNAME)];
    }

    public String getUsername() {
        return username;
    }
}
