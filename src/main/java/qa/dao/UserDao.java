package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.query.UserQueryCreator;
import qa.dao.query.convertor.UserQueryResultConvertor;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.user.UserFullDto;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryConfigurer;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDao extends DaoImpl<User> {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserDao(PropertySetterFactory propertySetterFactory,
                   SessionFactory sessionFactory) {
        super(HibernateSessionFactoryConfigurer.getSessionFactory(), new User(), propertySetterFactory.getSetter(new User()));
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Long create(User e) {
        return (Long) super.create(e);
    }

    @Nullable
    public User readFullUser(String username) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            UserFullDto userResult = UserQueryCreator
                    .fullUserQuery(session, username)
                    .uniqueResult();
            if (userResult == null) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
            return UserQueryResultConvertor
                    .dtoToUser(userResult, username);
        }
    }

    @Nullable
    public List<Question> readUserQuestions(long userId, int page) { // TODO order by like count

        /*
         *  if user not exist: questions.size() = 0; (NullResultException will not be thrown) - return null
         *  if questions not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Question> questions = new ArrayList<>();

            try {
                questions = UserQueryResultConvertor
                        .dtoToQuestion(
                                UserQueryCreator
                                        .questionsQuery(session, userId, page)
                                        .getResultList()
                        );
            }
            catch (NullResultException ex) { // questions not exist
                transaction.rollback();
                return questions;
            }

            if (questions.isEmpty()) { // author not exist
                transaction.rollback();
                return null;
            }

            transaction.commit();
            return questions;
        }
    }

    @Nullable
    public List<Answer> readUserAnswers(long userId, int page) {

        /*
         *  if user not exist: answers.size() = 0; (NullResultException will not be thrown) - return null
         *  if answers not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Answer> answers = new ArrayList<>();

            try {
                answers = UserQueryResultConvertor
                        .dtoToAnswers(
                                UserQueryCreator
                                        .answersQuery(session, userId, page)
                                        .list()
                        );
            }
            catch (NullResultException ex) { // answers not exist
                transaction.rollback();
                return answers;
            }

            if (answers.isEmpty()) { // author not exist
                transaction.rollback();
                return null;
            }

            transaction.commit();
            return answers;
        }
    }
}