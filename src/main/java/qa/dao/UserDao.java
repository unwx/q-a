package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
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
    private final int resultSize = 25;

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
            User userResult = convertDtoToUser(readUserQuery(session, username).uniqueResult(), username);
            if (userResult == null)
                return null;

            List<Answer> answers = convertDtoToAnswers(readAnswersQuery(session, userResult.getId()).list());
            List<Question> questions = convertDtoToQuestion(readQuestionsQuery(session, userResult.getId()).list());
            transaction.commit();

            return new User.Builder()
                    .id(userResult.getId())
                    .username(username)
                    .about(userResult.getAbout())
                    .answers(answers.size() > 0 ? answers : null)
                    .questions(questions.size() > 0 ? questions : null)
                    .build();
        }
    }

    @Nullable
    public List<Question> readUserQuestions(long userId, int startPage) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Question> questions = convertDtoToQuestion(readQuestionsQuery(session, userId, startPage).list());
            transaction.commit();

            return questions.size() > 0 ? questions : null;
        }
    }

    @Nullable
    public List<Answer> readUserAnswers(long userId, int startPage) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Answer> answers = convertDtoToAnswers(readAnswersQuery(session, userId, startPage).list());
            transaction.commit();

            return answers.size() > 0 ? answers : null;
        }
    }

    private Query<UserQuestionDto> readQuestionsQuery(Session session, long userId) {
        return readQuestionsQuery(session, userId, 0);
    }

    private Query<UserAnswerDto> readAnswersQuery(Session session, long userId) {
        return readAnswersQuery(session, userId, 0);
    }

    @SuppressWarnings("unchecked")
    private Query<UserFullDto> readUserQuery(Session session, String username) {
        String getUserHql = "select id as userId, about as about from User where username = :a";
        return session
                .createQuery(getUserHql)
                .setParameter("a", username)
                .unwrap(Query.class)
                .setResultTransformer(Transformers.aliasToBean(UserFullDto.class));
    }

    @SuppressWarnings("unchecked")
    private Query<UserQuestionDto> readQuestionsQuery(Session session, long userId, int startPage) {
        String getUserLastQuestions =
                """
                select q.id as questionId, q.title as title from Question as q\s\
                inner join q.author u\s\
                where u.id = :userId\s\
                order by q.lastActivity desc\
                """;
        return session
                .createQuery(getUserLastQuestions)
                .setParameter("userId", userId)
                .setFirstResult(startPage * resultSize)
                .setMaxResults(resultSize)
                .unwrap(Query.class)
                .setResultTransformer(Transformers.aliasToBean(UserQuestionDto.class));
    }

    @SuppressWarnings("unchecked")
    private Query<UserAnswerDto> readAnswersQuery(Session session, long userId, int startPage) {
        String getUserLastAnswers =
                """
                select a.id as answerId, substring(a.text, 1, 50) as text from Answer as a\s\
                inner join a.author u\s\
                where u.id = :userId\s\
                order by a.creationDate desc\
                """;
        return session
                .createQuery(getUserLastAnswers)
                .setParameter("userId", userId)
                .setFirstResult(startPage * resultSize)
                .setMaxResults(resultSize)
                .unwrap(Query.class)
                .setResultTransformer(Transformers.aliasToBean(UserAnswerDto.class));
    }

    @Nullable
    private User convertDtoToUser(@Nullable UserFullDto dto, String username) {
        if (dto == null)
            return null;

        return new User.Builder()
                .id(dto.getUserId())
                .username(username)
                .about(dto.getAbout())
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