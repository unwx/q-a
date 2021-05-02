package qa.dto.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import qa.domain.Answer;
import qa.domain.Question;
import qa.serializer.user.FullUserResponseSerializer;

import java.util.List;

@JsonSerialize(using = FullUserResponseSerializer.class)
public class UserFullResponse {

    @JsonProperty("id")
    private final Long userId;
    private final String username;
    private final String about;
    private final List<Question> questions;
    private final List<Answer> answers;

    public UserFullResponse(Long userId,
                            String username,
                            String about,
                            List<Question> questions,
                            List<Answer> answers) {
        this.userId = userId;
        this.username = username;
        this.about = about;
        this.questions = questions;
        this.answers = answers;
    }

    private UserFullResponse() {
        this.userId = null;
        this.username = null;
        this.about = null;
        this.questions = null;
        this.answers = null;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getAbout() {
        return about;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}
