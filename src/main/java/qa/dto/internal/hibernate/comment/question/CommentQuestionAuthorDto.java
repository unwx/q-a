package qa.dto.internal.hibernate.comment.question;

import java.util.Map;

public class CommentQuestionAuthorDto {

    private final String username;

    public static final String USERNAME = "c_u_username";

    public CommentQuestionAuthorDto(Object[] tuples,
                                    Map<String, Integer> aliasToIndexMap) {
        this.username = (String) tuples[aliasToIndexMap.get(USERNAME)];
    }

    public String getUsername() {
        return username;
    }
}
