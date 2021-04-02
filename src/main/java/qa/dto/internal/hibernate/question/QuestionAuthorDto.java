package qa.dto.internal.hibernate.question;

import java.util.Map;

public class QuestionAuthorDto {

    private final String username;

    public static final String USERNAME = "que_u_username";

    public QuestionAuthorDto(Object[] tuples,
                                          Map<String, Integer> aliasToIndexMap) {
        this.username = (String) tuples[aliasToIndexMap.get(USERNAME)];
    }

    public String getUsername() {
        return username;
    }
}
