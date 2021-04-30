package qa.dao.query.manager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.UserQueryCreator;
import qa.dao.query.convertor.UserQueryResultConvertor;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.dto.internal.hibernate.user.UserAnswerDto;
import qa.dto.internal.hibernate.user.UserFullDto;
import qa.dto.internal.hibernate.user.UserQuestionDto;

import java.util.List;

public class UserQueryManager {

    private UserQueryManager() {}

    public static Query<UserFullDto> fullUserQuery(Session session, String username) {
        return UserQueryCreator.fullUserQuery(session, username);
    }

    public static Query<UserQuestionDto> questionsQuery(Session session, long userId, int page) {
        return UserQueryCreator.questionsQuery(session, userId, page);
    }

    public static Query<UserAnswerDto> answersQuery(Session session, long userId, int page) {
        return UserQueryCreator.answersQuery(session, userId, page);
    }

    public static User dtoToUser(UserFullDto dto, String username) {
        return UserQueryResultConvertor.dtoToUser(dto, username);
    }

    public static List<Answer> dtoToAnswers(List<UserAnswerDto> dto) {
        return UserQueryResultConvertor.dtoToAnswers(dto);
    }

    public static List<Question> dtoToQuestion(List<UserQuestionDto> dto) {
        return UserQueryResultConvertor.dtoToQuestion(dto);
    }
}
