package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.query.manager.UserQueryManager;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.User;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.user.UserAnswerDto;
import qa.dto.internal.hibernate.user.UserFullDto;
import qa.dto.internal.hibernate.user.UserQuestionDto;
import qa.exceptions.dao.NullResultException;

import java.util.Collections;
import java.util.List;

@Component
public class UserDao extends DaoImpl<User> {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserDao(PropertySetterFactory propertySetterFactory,
                   SessionFactory sessionFactory) {

        super(sessionFactory, propertySetterFactory.getSetter(new User()));
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Long create(User e) {
        return (Long) super.create(e);
    }

    @Nullable
    public User readFullUser(String username) {

        final UserFullDto dto;
        final User user;

        try(Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            dto = UserQueryManager
                    .fullUserQuery(session, username)
                    .uniqueResult();

            if (dto == null) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
        }

        user = UserQueryManager.dtoToUser(dto, username);
        return user;
    }

    @Nullable
    public List<Question> readUserQuestions(long userId, int page) {

        /*
         *  if user not exist: questions.size() = 0; (NullResultException will not be thrown) - return null
         *  if questions not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        final List<UserQuestionDto> dto;
        final List<Question> questions;

        try(Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            try {
                dto = UserQueryManager
                        .questionsQuery(session, userId, page)
                        .list();
            }
            catch (NullResultException ex) { // questions not exist
                transaction.rollback();
                return Collections.emptyList();
            }

            if (dto.isEmpty()) { // author not exist
                transaction.rollback();
                return null;
            }

            transaction.commit();
        }

        questions = UserQueryManager.dtoToQuestion(dto);
        return questions;
    }

    @Nullable
    public List<Answer> readUserAnswers(long userId, int page) {

        /*
         *  if user not exist: answers.size() = 0; (NullResultException will not be thrown) - return null
         *  if answers not exist: NullResultException - return empty list.
         *  if exist: return result.
         */

        final List<UserAnswerDto> dto;
        final List<Answer> answers;

        try(Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            try {
                dto = UserQueryManager
                        .answersQuery(session, userId, page)
                        .list();
            }
            catch (NullResultException ex) { // answers not exist
                transaction.rollback();
                return Collections.emptyList();
            }

            if (dto.isEmpty()) { // author not exist
                transaction.rollback();
                return null;
            }

            transaction.commit();
        }

        answers = UserQueryManager.dtoToAnswers(dto);
        return answers;
    }
}