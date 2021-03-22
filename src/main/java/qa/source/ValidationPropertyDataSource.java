package qa.source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.source.validation.*;

@Component
public class ValidationPropertyDataSource {

    private final AuthenticationPropertyDataSource authentication;
    private final UserPropertyDataSource user;
    private final QuestionPropertyDataSource question;
    private final AnswerPropertyDataSource answer;
    private final CommentPropertyDataSource comment;



    @Autowired
    public ValidationPropertyDataSource(AuthenticationPropertyDataSource authentication,
                                        UserPropertyDataSource user,
                                        QuestionPropertyDataSource question,
                                        AnswerPropertyDataSource answer,
                                        CommentPropertyDataSource comment) {
        this.authentication = authentication;
        this.user = user;
        this.question = question;
        this.answer = answer;
        this.comment = comment;
    }

    public AuthenticationPropertyDataSource getAuthentication() {
        return authentication;
    }

    public UserPropertyDataSource getUser() {
        return user;
    }

    public QuestionPropertyDataSource getQuestion() {
        return question;
    }

    public AnswerPropertyDataSource getAnswer() {
        return answer;
    }

    public CommentPropertyDataSource getComment() {
        return comment;
    }
}
