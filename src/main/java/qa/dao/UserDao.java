package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.transformer.user.UserAnswerDtoTransformer;
import qa.dto.internal.hibernate.transformer.user.UserFullDtoTransformer;
import qa.dto.internal.hibernate.transformer.user.UserQuestionDtoTransformer;
import qa.dto.internal.hibernate.user.UserAnswerDto;
import qa.dto.internal.hibernate.user.UserFullDto;
import qa.dto.internal.hibernate.user.UserQuestionDto;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("deprecation")
public class UserDao extends DaoImpl<User> {

    private final SessionFactory sessionFactory;
    private final int resultSize = 10;

    @Autowired
    public UserDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new User(), propertySetterFactory.getSetter(new User()));
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(User e) {
        return (Long) super.create(e);
    }

    @Nullable
    public User readFullUser(String username) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            UserFullDto userResult = getFullUserQuery(session, username).uniqueResult();
            if (userResult == null) {
                transaction.rollback();
                return null;
            }
            transaction.commit();

            return convertDtoToUser(userResult, username);
        }
    }

    @NotNull
    public List<Question> readUserQuestions(long userId, int page) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Question> questions = convertDtoToQuestion(readQuestionsQuery(session, userId, page).list());
            transaction.commit();

            return questions;
        }
    }

    @NotNull
    public List<Answer> readUserAnswers(long userId, int page) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Answer> answers = convertDtoToAnswers(readAnswersQuery(session, userId, page).list());
            transaction.commit();

            return answers;
        }
    }

    @SuppressWarnings("unchecked")
    private Query<UserFullDto> getFullUserQuery(Session session, String username) {
        String getFullUserSql =
                """
                SELECT\s\
                    u.id AS usr_id, u.about AS usr_about,\s\
                    a.id AS usr_a_id, a.text AS usr_a_text,\s\
                    q.id AS usr_q_id, q.title AS usr_q_title\s\
                FROM usr AS u\s\
                LEFT JOIN LATERAL 
                    (SELECT id, SUBSTRING(a.text, 1, 50) AS text, author_id\s\
                    FROM answer AS a\s\
                    WHERE author_id = u.id LIMIT :RN) AS a ON TRUE
                LEFT JOIN LATERAL 
                    (SELECT id, title, author_id\s\
                    FROM question AS q\s\
                    WHERE a.author_id = u.id LIMIT :RN) AS q ON TRUE
                WHERE u.username = :username   
                """;
        return session.createSQLQuery(getFullUserSql)
                .unwrap(Query.class)
                .setParameter("username", username)
                .setParameter("RN", resultSize)
                .setResultTransformer(new UserFullDtoTransformer());
    }

    @SuppressWarnings("unchecked")
    private Query<UserQuestionDto> readQuestionsQuery(Session session, long userId, int page) {
        String getUserLastQuestions =
                """
                SELECT q.id as usr_q_id, q.title AS usr_q_title FROM question AS q\s\
                INNER JOIN usr AS u ON q.author_id = u.id\s\
                WHERE u.id = :userId\s\
                ORDER BY q.last_activity DESC\
                """;
        return session
                .createSQLQuery(getUserLastQuestions)
                .unwrap(Query.class)
                .setParameter("userId", userId)
                .setFirstResult(page * resultSize)
                .setMaxResults(resultSize)
                .setResultTransformer(new UserQuestionDtoTransformer());
    }

    @SuppressWarnings("unchecked")
    private Query<UserAnswerDto> readAnswersQuery(Session session, long userId, int page) {
        String getUserLastAnswers =
                """
                SELECT a.id AS usr_a_id, substring(a.text, 1, 50) AS usr_a_text FROM answer AS a\s\
                INNER JOIN usr AS u ON a.author_id = u.id\s\
                WHERE u.id = :userId\s\
                ORDER BY a.creation_date DESC\
                """;
        return session
                .createSQLQuery(getUserLastAnswers)
                .unwrap(Query.class)
                .setParameter("userId", userId)
                .setFirstResult(page * resultSize)
                .setMaxResults(resultSize)
                .setResultTransformer(new UserAnswerDtoTransformer());
    }

    private User convertDtoToUser(UserFullDto dto, String username) {
        return new User.Builder()
                .id(dto.getUserId())
                .username(username)
                .about(dto.getAbout())
                .questions(convertDtoToQuestion(dto.getQuestions()))
                .answers(convertDtoToAnswers(dto.getAnswers()))
                .build();
    }

    private List<Answer> convertDtoToAnswers(List<UserAnswerDto> dto) {
        List<Answer> answers = new ArrayList<>(dto.size());
        dto.forEach((d) -> answers.add(new Answer.Builder()
                .id(d.getAnswerId())
                .text(d.getText())
                .build()));
        return answers;
    }

    private List<Question> convertDtoToQuestion(List<UserQuestionDto> dto) {
        List<Question> questions = new ArrayList<>(dto.size());
        dto.forEach((d) -> questions.add(new Question.Builder()
                .id(d.getQuestionId())
                .title(d.getTitle())
                .build()));
        return questions;
    }
}