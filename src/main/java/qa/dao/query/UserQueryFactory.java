package qa.dao.query;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.dto.internal.hibernate.transformer.user.UserAnswerDtoTransformer;
import qa.dto.internal.hibernate.transformer.user.UserFullDtoTransformer;
import qa.dto.internal.hibernate.transformer.user.UserQuestionDtoTransformer;
import qa.dto.internal.hibernate.user.UserAnswerDto;
import qa.dto.internal.hibernate.user.UserFullDto;
import qa.dto.internal.hibernate.user.UserQuestionDto;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings({"deprecation", "unchecked"})
public class UserQueryFactory {

    private static final int RESULT_SIZE = 10;
    private final ResultConvertor resultConvertor = new ResultConvertor();

    public UserQueryFactory() {
    }


    public Query<UserFullDto> fullUserQuery(Session session, String username) {
        String getFullUserSql =
                """
                SELECT\s\
                    u.id AS usr_id, u.about AS usr_about,\s\
                    a.id AS usr_a_id, a.text AS usr_a_text,\s\
                    q.id AS usr_q_id, q.title AS usr_q_title\s\
                FROM usr AS u\s\
                LEFT JOIN LATERAL\s\
                    (SELECT id, SUBSTRING(a.text, 1, 50) AS text\s\
                    FROM answer AS a\s\
                    WHERE author_id = u.id LIMIT :limit) AS a ON TRUE\s\
                LEFT JOIN LATERAL\s\
                    (SELECT id, title\s\
                    FROM question AS q\s\
                    WHERE author_id = u.id LIMIT :limit) AS q ON TRUE\s\
                WHERE u.username = :username\
                """;
        return session.createSQLQuery(getFullUserSql)
                .unwrap(Query.class)
                .setParameter("username", username)
                .setParameter("limit", RESULT_SIZE)
                .setResultTransformer(new UserFullDtoTransformer());
    }

    public Query<UserQuestionDto> questionsQuery(Session session, long userId, int page) {
        String getUserLastQuestions =
                """
                SELECT\s\
                    q.id AS usr_q_id, q.title AS usr_q_title\s\
                FROM usr AS u\s\
                LEFT JOIN LATERAL\s\
                    (\
                    SELECT id, title\s\
                    FROM question\s\
                    WHERE author_id = u.id\s\
                    ORDER BY last_activity DESC\s\
                    LIMIT :limit OFFSET :offset\s\
                    ) AS q ON TRUE\s\
                WHERE u.id = :userId\s\
                """;
        return session
                .createSQLQuery(getUserLastQuestions)
                .unwrap(Query.class)
                .setParameter("userId", userId)
                .setParameter("limit", RESULT_SIZE)
                .setParameter("offset", page * RESULT_SIZE)
                .setResultTransformer(new UserQuestionDtoTransformer());
    }

    public Query<UserAnswerDto> answersQuery(Session session, long userId, int page) {
        String getUserLastAnswers =
                """
                SELECT\s\
                    a.id AS usr_a_id, a.s_text AS usr_a_text\s\
                FROM usr AS u\s\
                LEFT JOIN LATERAL\s\
                    (\
                    SELECT id, SUBSTRING(text, 1, 50) AS s_text\s\
                    FROM answer\s\
                    WHERE author_id = u.id\s\
                    ORDER BY creation_date DESC\s\
                    LIMIT :limit OFFSET :offset\s\
                    ) AS a ON TRUE\s\
                WHERE u.id = :userId\s\
                """;

        return session
                .createSQLQuery(getUserLastAnswers)
                .unwrap(Query.class)
                .setParameter("userId", userId)
                .setParameter("limit", RESULT_SIZE)
                .setParameter("offset", page * RESULT_SIZE)
                .setResultTransformer(new UserAnswerDtoTransformer());
    }

    public ResultConvertor getConvertor() {
        return resultConvertor;
    }

    public static class ResultConvertor {

        public User dtoToUser(UserFullDto dto, String username) {
            return new User.Builder()
                    .id(dto.getUserId())
                    .username(username)
                    .about(dto.getAbout())
                    .questions(dtoToQuestion(dto.getQuestions()))
                    .answers(dtoToAnswers(dto.getAnswers()))
                    .build();
        }

        public List<Answer> dtoToAnswers(List<UserAnswerDto> dto) {
            List<Answer> answers = new ArrayList<>(dto.size());
            dto.forEach((d) -> answers.add(new Answer.Builder()
                    .id(d.getAnswerId())
                    .text(d.getText())
                    .build()));
            return answers;
        }

        public List<Question> dtoToQuestion(List<UserQuestionDto> dto) {
            List<Question> questions = new ArrayList<>(dto.size());
            dto.forEach((d) -> questions.add(new Question.Builder()
                    .id(d.getQuestionId())
                    .title(d.getTitle())
                    .build()));
            return questions;
        }
    }
}
