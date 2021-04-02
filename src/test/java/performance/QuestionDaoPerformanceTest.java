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
import qa.dto.internal.hibernate.question.QuestionViewDto;
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
    void getFullQuestionTest_SQL_QUERY_ONLY() {
        createQuestionWithCommentAndAnswer();
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Monitor oneQMon = null;
            for (int i = 0; i < 5000; i++) {
                oneQMon = MonitorFactory.start("one query test");
                getFullQuestionSqlExecute(session);
                if (i % 20 == 0) {
                    session.flush();
                    session.clear();
                }
                oneQMon.stop();
            }

            Monitor twoQMon = null;
            for (int i = 0; i < 5000; i++) {
                twoQMon = MonitorFactory.start("two query test");
                getFullQuestion2QuerySqlExecute(session);
                if (i % 20 == 0) {
                    session.flush();
                    session.clear();
                }
                twoQMon.stop();
            }

            System.out.printf("ONE QUERY RESULT: %sms%n", oneQMon);
            System.out.printf("TWO QUERY RESULT: %sms%n", twoQMon);
            transaction.commit();
        }
    }

    @Test
    void getQuestionsPagesTest_ALL() {
        createManyQuestionsWithManyAnswers();

        Monitor monitor = MonitorFactory.start("get questions test");
        List<QuestionViewDto> dtos = questionDao.getQuestionViewsDto(12);
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

    private void getFullQuestionSqlExecute(Session session) {
        String getQuestionSql =
                """
                WITH
                 answ AS (\
                    SELECT\s\
                        id,\s\
                        text,\s\
                        answered,\s\
                        creation_date,\s\
                        question_id,\s\
                        ROW_NUMBER() OVER (PARTITION BY question_id ORDER BY creation_date DESC) rn\s\
                    FROM answer),\s\
                 comm AS (\
                    SELECT\s\
                        c.id,\s\
                        c.text,\s\
                        c.creation_date,\s\
                        c.answer_id,\s\
                        u.username,\s\
                        ROW_NUMBER() OVER (PARTITION BY c.answer_id ORDER BY c.creation_date DESC) rn\s\
                    FROM comment AS c\s\
                        INNER JOIN usr u ON u.id = c.author_id)\s\
                SELECT\s\
                    q.title AS q_title, q.text AS q_text,\s\
                    q.tags AS q_tags, q.creation_date AS q_c_date,\s\
                    q.last_activity AS activity,\s\
                    
                    c.id as c_id, c.text as c_text, c.creation_date as c_c_date,\s\
                    u.username as q_username,\s\
                    cu.username as c_a_username,\s\
                    
                    answ.id AS a_id, answ.text AS a_text,\s\
                    answ.answered AS a_answered, answ.creation_date as a_c_date,\s\
                    
                    comm.id AS c_id, comm.text AS c_text,\s\
                    comm.creation_date AS c_c_date, comm.username AS u_username\s\
                FROM question AS q\s\
                INNER JOIN comment AS c ON q.id = c.question_id\s\
                INNER JOIN usr AS u ON q.author_id = u.id\s\
                INNER JOIN usr AS cu ON c.author_id = cu.id\s\
                INNER JOIN answ ON q.id = answ.question_id AND answ.rn <= :answerLimit\s\
                INNER JOIN comm ON answ.id = comm.answer_id AND comm.rn <= :commentLimit\s\
                WHERE q.id = :questionId\s\
                ORDER BY c.creation_date desc\
                """;
        session.createSQLQuery(getQuestionSql)
                .setParameter("answerLimit", 7)
                .setParameter("commentLimit", 3)
                .setParameter("questionId", 1);
    }

    private void getFullQuestion2QuerySqlExecute(Session session) {

        String getQuestionPartHql =
                """
                select\s\
                q.title as q_title, q.text as q_text, q.tags as q_tags,\s\
                q.creationDate as q_c_date, q.lastActivity as q_l_activity,\s\
                c.id as c_id, c.text as c_text, c.creationDate as c_c_date,\s\
                u.username as q_a_username,\s\
                cu.username as c_a_username\s\
                from Question q\s\
                inner join q.author u\s\
                inner join q.comments c\s\
                inner join c.author cu\s\
                where q.id = :questionId\s\
                order by c.creationDate desc\s\
                """;

        String getAnswersSql =
                """
                WITH
                 answ AS (\
                    SELECT\s\
                        id,\s\
                        text,\s\
                        answered,\s\
                        creation_date,\s\
                        question_id,\s\
                        ROW_NUMBER() OVER (PARTITION BY question_id ORDER BY creation_date DESC) rn\s\
                    FROM answer),\s\
                 comm AS (\
                    SELECT\s\
                        c.id,\s\
                        c.text,\s\
                        c.creation_date,\s\
                        c.answer_id,\s\
                        u.username,\s\
                        ROW_NUMBER() OVER (PARTITION BY c.answer_id ORDER BY c.creation_date DESC) rn\s\
                    FROM comment AS c\s\
                        INNER JOIN usr u ON u.id = c.author_id)\s\
                SELECT\s\
                    answ.id AS a_id, answ.text AS a_text,\s\
                    answ.answered AS a_answered, answ.creation_date as a_c_date,\s\
                    
                    comm.id AS c_id, comm.text AS c_text,\s\
                    comm.creation_date AS c_c_date, comm.username AS u_username\s\
                FROM question AS q\s\
                INNER JOIN answ ON q.id = answ.question_id AND answ.rn <= :answerLimit\s\
                INNER JOIN comm ON answ.id = comm.answer_id AND comm.rn <= :commentLimit\s\
                WHERE q.id = :questionId\
                """;
        session.createQuery(getQuestionPartHql)
                .setParameter("questionId", 1L)
                .list();
        session.createSQLQuery(getAnswersSql)
                .setParameter("answerLimit", 7)
                .setParameter("commentLimit", 3)
                .setParameter("questionId", 1)
                .list();
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
