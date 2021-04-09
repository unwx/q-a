package performance;


import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import qa.config.spring.SpringConfig;
import qa.dao.QuestionDao;
import qa.domain.Question;
import qa.domain.QuestionView;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Date;
import java.util.List;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class QuestionDaoPerformanceTest {

    @Autowired
    private QuestionDao questionDao;
    private final SessionFactory sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();

    @BeforeEach
    void truncate() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table answer cascade").executeUpdate();
            session.createSQLQuery("truncate table comment cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void getFullQuestionDaoTest() {
        createQuestionWithCommentAndAnswer();

        Monitor monitor = null;
        for (int i = 0; i < 5000; i++) {
            monitor = MonitorFactory.start("get full question test");
            Question q = questionDao.getFullQuestion(1L);
            monitor.stop();
        }
        System.out.printf("RESULT: %s", monitor);
    }

    @Test
    void getQuestionsPagesTest_ALL() {
        createManyQuestionsWithManyAnswers();

        Monitor monitor = MonitorFactory.start("get questions test");
        List<QuestionView> dtos = questionDao.getQuestionViewsDto(12);
        monitor.stop();
        System.out.printf("RESULT: %s", monitor);
    }

    private void createQuestionWithCommentAndAnswer() {
        try(Session session = sessionFactory.openSession()) {
            String createUserSql =
                    """
                    insert into usr (id, about, username) values (1, null, 'username')
                    """;
            String createQuestionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (1, '%s', '%s', 'tags', 'text', 'title', 1)
                    """.formatted(new Date(), new Date());
            String createAnswerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, '%s', 'text', 1, 1)
                    """.formatted(new Date());
            String createQuestionCommentSql =
                    """
                    insert into comment (comment_type, id, text, author_id, answer_id, creation_date, question_id)\s\
                    values ('question', :id, 'text', 1, null, '%s', 1)
                    """.formatted(new Date());
            String createAnswerCommentSql =
                    """
                    insert into comment (comment_type, id, text, author_id, answer_id, creation_date, question_id)\s\
                    values ('answer', :id, 'text', 1, :answerId, '%s', null)
                    """.formatted(new Date());
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery(createUserSql).executeUpdate();
            session.createSQLQuery(createQuestionSql).executeUpdate();

            long commentId = 0;
            for (int i = 0; i < 15; i++) {
                session.createSQLQuery(createAnswerSql).setParameter("id", (long) i).executeUpdate();
                session.createSQLQuery(createQuestionCommentSql).setParameter("id", commentId).executeUpdate();
                commentId++;
                for (int y = 0; y < 20; y++) {
                    session.createSQLQuery(createAnswerCommentSql)
                            .setParameter("answerId", i)
                            .setParameter("id", commentId)
                            .executeUpdate();
                    commentId++;
                }
                session.flush();
            }
            transaction.commit();
        }
    }

    private void createManyQuestionsWithManyAnswers() {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String createUserSql =
                    """
                    insert into usr (id, about, username) values (1, null, 'username')
                    """;
            String createQuestionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (:id, :date, :date, 'tags', 'text', 'title', 1)
                    """;
            String createAnswerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, :date, 'text', 1, :questionId)
                    """;
            session.createSQLQuery(createUserSql).executeUpdate();

            int answerCounter = 0;
            for (int i = 0; i < 300; i++) {
                session.createSQLQuery(createQuestionSql)
                        .setParameter("id", (long) i)
                        .setParameter("date", new Date(123123123123L * i))
                        .executeUpdate();
                for (int y = 0; y < (int) (150 + Math.random() * 1500); y++) {
                    session.createSQLQuery(createAnswerSql)
                            .setParameter("id", answerCounter)
                            .setParameter("date", new Date(9876654332L * i))
                            .setParameter("questionId", (long) i)
                            .executeUpdate();
                    answerCounter++;
                }
                session.flush();
                session.clear();
            }
            transaction.commit();
        }
    }
}
