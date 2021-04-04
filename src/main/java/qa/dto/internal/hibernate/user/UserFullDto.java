package qa.dto.internal.hibernate.user;

import qa.dto.internal.hibernate.AliasUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserFullDto {

    private final Long userId;
    private final String about;

    private final List<UserQuestionDto> questions = new ArrayList<>();
    private final List<UserAnswerDto> answers = new ArrayList<>();

    public static final String ID = "usr_id";
    public static final String ABOUT = "usr_about";

    public UserFullDto(Object[] tuples,
                       Map<String, Integer> aliasToIndexMap) {
        this.userId = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.about = AliasUtil.setIfNotNull(ABOUT, aliasToIndexMap, tuples);
    }

    public Long getUserId() {
        return userId;
    }

    public String getAbout() {
        return about;
    }

    public List<UserQuestionDto> getQuestions() {
        return questions;
    }

    public List<UserAnswerDto> getAnswers() {
        return answers;
    }
}
