package qa.dto.response.user;

import qa.domain.Answer;
import qa.domain.Question;

import java.util.List;

public class FullUserResponse {

    private final String username;
    private final String about;
    private final List<Question> questions;
    private final List<Answer> answers;

    public FullUserResponse(String username,
                            String about,
                            List<Question> questions,
                            List<Answer> answers) {
        this.username = username;
        this.about = about;
        this.questions = questions;
        this.answers = answers;
    }

    protected FullUserResponse() {
        this.username = null;
        this.about = null;
        this.questions = null;
        this.answers = null;
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
