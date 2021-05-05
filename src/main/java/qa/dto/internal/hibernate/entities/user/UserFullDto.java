package qa.dto.internal.hibernate.entities.user;

import qa.dto.internal.hibernate.AliasUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class UserFullDto {

    private final Long userId;
    private final String about;

    private final List<UserQuestionDto> questions = new ArrayList<>();
    private final List<UserAnswerDto> answers = new ArrayList<>();

    public static final String ID = "usr_id";
    public static final String ABOUT = "usr_about";

    private final HashSet<Long> questionIds = new HashSet<>();
    private final HashSet<Long> answerIds = new HashSet<>();

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

    public void addAnswerIfAbsent(UserAnswerDto dto) {
        if (!answerIds.contains(dto.getAnswerId())) {
            answers.add(dto);
            answerIds.add(dto.getAnswerId());
        }
    }

    public void addQuestionIfAbsent(UserQuestionDto dto) {
        if (!questionIds.contains(dto.getQuestionId())) {
            questions.add(dto);
            questionIds.add(dto.getQuestionId());
        }
    }
}
