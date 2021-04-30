package qa.dao.query.convertor;

import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.dto.internal.hibernate.user.UserAnswerDto;
import qa.dto.internal.hibernate.user.UserFullDto;
import qa.dto.internal.hibernate.user.UserQuestionDto;

import java.util.ArrayList;
import java.util.List;

public class UserQueryResultConvertor {

    private UserQueryResultConvertor() {}

    public static User dtoToUser(UserFullDto dto, String username) {
        return new User.Builder()
                .id(dto.getUserId())
                .username(username)
                .about(dto.getAbout())
                .questions(dtoToQuestion(dto.getQuestions()))
                .answers(dtoToAnswers(dto.getAnswers()))
                .build();
    }

    public static List<Answer> dtoToAnswers(List<UserAnswerDto> dto) {
        final List<Answer> answers = new ArrayList<>(dto.size());
        dto.forEach((d) -> answers.add(new Answer.Builder()
                .id(d.getAnswerId())
                .text(d.getText())
                .build()));
        return answers;
    }

    public static List<Question> dtoToQuestion(List<UserQuestionDto> dto) {
        final List<Question> questions = new ArrayList<>(dto.size());
        dto.forEach((d) -> questions.add(new Question.Builder()
                .id(d.getQuestionId())
                .title(d.getTitle())
                .build()));
        return questions;
    }

    public static User usernameToAuthor(String username) {
        return new User.Builder().username(username).build();
    }
}
