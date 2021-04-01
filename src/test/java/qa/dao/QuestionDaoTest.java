package qa.dao;

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
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.Question;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.lang.reflect.Field;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringConfig.class)
public class QuestionDaoTest {

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
    void getFullQuestion_AssertCorrectData() throws IllegalAccessException, NoSuchFieldException {
        Field commentStartResultSizeField = QuestionDao.class.getDeclaredField("commentResultSize");
        commentStartResultSizeField.setAccessible(true);
        int commentStartResultSize = (int) commentStartResultSizeField.get(questionDao);

        createQuestionWithCommentAndAnswer();
        Question q = questionDao.getFullQuestion(1L);

        assertThat(q, notNullValue());
        assertThat(q.getId(), equalTo(1L));
        assertThat(q.getText(), notNullValue());
        assertThat(q.getTitle(), notNullValue());
        assertThat(q.getCreationDate(), notNullValue());
        assertThat(q.getLastActivity(), notNullValue());
        assertThat(q.getTags(), notNullValue());

        assertThat(q.getAuthor(), notNullValue());
        assertThat(q.getAuthor().getUsername(), notNullValue());

        assertThat(q.getAnswers(), notNullValue());
        for (Answer a : q.getAnswers()) {
            assertThat(a.getId(), notNullValue());
            assertThat(a.getText(), notNullValue());
            assertThat(a.getAnswered(), notNullValue());
            assertThat(a.getCreationDate(), notNullValue());
            assertThat(a.getAuthor(), notNullValue());
            assertThat(a.getAuthor().getUsername(), notNullValue());

            assertThat(a.getComments(), notNullValue());
            assertThat(a.getComments().size(), lessThan(commentStartResultSize + 1));
            for (CommentAnswer c : a.getComments()) {
                assertThat(c.getId(), notNullValue());
                assertThat(c.getText(), notNullValue());
                assertThat(c.getCreationDate(), notNullValue());
                assertThat(c.getAuthor(), notNullValue());
                assertThat(c.getAuthor().getUsername(), notNullValue());
            }
        }

        assertThat(q.getComments(), notNullValue());
        assertThat(q.getComments().size(), lessThan(commentStartResultSize + 1));
        for (int i = 0; i < commentStartResultSize; i++) {
            assertThat(q.getComments().get(i).getAuthor(), notNullValue());
            assertThat(q.getComments().get(i).getAuthor().getUsername(), notNullValue());
            assertThat(q.getComments().get(i).getId(), notNullValue());
            assertThat(q.getComments().get(i).getText(), notNullValue());
            assertThat(q.getComments().get(i).getCreationDate(), notNullValue());
        }
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
}
