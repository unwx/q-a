package qa.dto.response.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import qa.domain.Answer;
import qa.domain.Question;
import qa.serializer.FullUserSerializer;

import java.util.List;

@JsonSerialize(using = FullUserSerializer.class)
public class FullUserResponse {

    private final Long id;
    private final String username;
    private final String about;
    private final List<Question> questions;
    private final List<Answer> answers;

    public FullUserResponse(Long id,
                            String username,
                            String about,
                            List<Question> questions,
                            List<Answer> answers) {
        this.id = id;
        this.username = username;
        this.about = about;
        this.questions = questions;
        this.answers = answers;
    }

    protected FullUserResponse() {
        this.id = null;
        this.username = null;
        this.about = null;
        this.questions = null;
        this.answers = null;
    }

    public Long getId() {
        return id;
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
