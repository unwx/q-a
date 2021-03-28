package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
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
    @SuppressWarnings("unchecked")
    public User readFullUser(String username) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Object[] userResult = (Object[]) readUserQuery(session, username).uniqueResult();
            if (userResult == null)
                return null;

            Long userId = (Long) userResult[0];
            List<Answer> answers = objToAnswer((List<Object[]>) readAnswersQuery(session, userId).list());
            List<Question> questions = objToQuestion((List<Object[]>) readQuestionsQuery(session, userId).list());
            transaction.commit();

            User user = new User();
            user.setUsername(username);
            user.setId(userId);

            if (!questions.isEmpty()) {
                user.setQuestions(questions);
            }
            if (!answers.isEmpty()) {
                user.setAnswers(answers);
            }
            return user;
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public List<Question> readUserQuestions(Long userId, int startPage) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Question> questions = objToQuestion((List<Object[]>) readQuestionsQuery(session, userId, startPage).list());
            transaction.commit();

            return questions.size() > 0 ? questions : null;
        }
    }


    private Query<?> readUserQuery(Session session, String username) {
        String getUserHql = "select id, about from User where username=:a";
        return session.createQuery(getUserHql).setParameter("a", username);
    }

    private Query<?> readQuestionsQuery(Session session, long userId) {
        return readQuestionsQuery(session, userId, 0);
    }

    private Query<?> readAnswersQuery(Session session, long userId) {
        String getUserLastAnswers =
                """
                select a.id, substring(a.text, 1, 50) from answer as a\s\
                where a.author_id=:a\s\
                order by a.creation_date desc\s\
                limit 25\
                """;
        return session.createSQLQuery(getUserLastAnswers).setParameter("a", userId);
    }

    private Query<?> readQuestionsQuery(Session session, long userId, int startPage) {
        String getUserLastQuestions =
                """
                select a.id, a.title from question as a\s\
                where a.author_id=:a\s\
                order by a.last_activity desc\s\
                limit :p offset :p1\
                """;
        return session
                .createSQLQuery(getUserLastQuestions)
                .setParameter("a", userId)
                .setParameter("p", resultSize)
                .setParameter("p1", startPage * resultSize);
    }

    private List<Question> objToQuestion(List<Object[]> questionsObj) {
        List<Question> questions = new ArrayList<>(questionsObj.size());
        questionsObj.forEach((q) -> {
            Question question = new Question.Builder()
                    .id(((BigInteger) q[0]).longValue())
                    .title((String) q[1])
                    .build();
            questions.add(question);
        });
        return questions;
    }

    private List<Answer> objToAnswer(List<Object[]> answersObj) {
        List<Answer> answers = new ArrayList<>(answersObj.size());
        answersObj.forEach((a) -> {
            Answer answer = new Answer.Builder()
                    .id(((BigInteger) a[0]).longValue())
                    .text((String) a[1])
                    .build();
            answers.add(answer);
        });
        return answers;
    }
}
