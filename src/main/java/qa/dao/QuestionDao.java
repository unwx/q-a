package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.dao.query.AnswerQueryCreator;
import qa.dao.query.QuestionQueryCreator;
import qa.dao.query.convertor.AnswerQueryResultConvertor;
import qa.dao.query.convertor.QuestionQueryResultConvertor;
import qa.domain.Answer;
import qa.domain.Question;
import qa.domain.QuestionView;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.question.QuestionWithCommentsDto;
import qa.exceptions.dao.NullResultException;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionDao extends DaoImpl<Question> {

    private final SessionFactory sessionFactory;

    @Autowired
    public QuestionDao(PropertySetterFactory propertySetterFactory) {
        super(HibernateSessionFactoryUtil.getSessionFactory(), new Question(), propertySetterFactory.getSetter(new Question()));
        this.sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
    }

    @Override
    public Long create(Question e) {
        return (Long) super.create(e);
    }

    public boolean isExist(Long id) {
        return super.isExist(new Where("id", id, WhereOperator.EQUALS), "Question");
    }

    @Nullable
    public Question getFullQuestion(long questionId) {

        /*
         *  if question not exist: - return null
         *  if answers not exist: NullResultException - return question with answers (empty list)
         */

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            QuestionWithCommentsDto questionResult = QuestionQueryCreator
                    .questionWithCommentsQuery(session, questionId)
                    .uniqueResult();
            if (questionResult == null) {
                transaction.rollback();
                return null;
            }

            List<Answer> answers = new ArrayList<>();
            try {
                answers = AnswerQueryResultConvertor
                        .dtoToAnswerList(
                                AnswerQueryCreator
                                        .answersWithCommentsQuery(session, questionId)
                                        .list()
                        );
            } catch (NullResultException ignored) {}

            transaction.commit();
            Question question = QuestionQueryResultConvertor
                    .dtoToQuestion(questionResult, questionId);
            question.setAnswers(answers);
            return question;
        }
    }

    @NotNull
    public List<QuestionView> getQuestionViewsDto(int page) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<QuestionView> views = QuestionQueryResultConvertor
                    .dtoToQuestionViewList(QuestionQueryCreator
                            .questionsViewsQuery(session, page)
                            .list()
                    );
            transaction.commit();
            return views;
        }
    }


}
