package qa.dto.internal.hibernate.comment.answer;

import java.util.Map;

public class CommentAnswerAuthorDto {

    private final String username;

    public static final String USERNAME = "c_u_username";

    public CommentAnswerAuthorDto(Object[] tuples,
                                  Map<String, Integer> aliasToIndexMap) {
        this.username = (String) tuples[aliasToIndexMap.get(USERNAME)];
    }

    public String getUsername() {
        return username;
    }
}
