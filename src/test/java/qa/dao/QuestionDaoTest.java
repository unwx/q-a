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
import qa.domain.CommentQuestion;
import qa.domain.Question;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        Field commentResultSizeField = QuestionDao.class.getDeclaredField("commentResultSize");
        commentResultSizeField.setAccessible(true);
        int commentResultSize = (int) commentResultSizeField.get(questionDao);

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
            assertThat(a.getComments().size(), lessThan(commentResultSize + 1));
            for (CommentAnswer c : a.getComments()) {
                assertThat(c.getId(), notNullValue());
                assertThat(c.getText(), notNullValue());
                assertThat(c.getCreationDate(), notNullValue());
                assertThat(c.getAuthor(), notNullValue());
                assertThat(c.getAuthor().getUsername(), notNullValue());
            }
        }

        assertThat(q.getComments(), notNullValue());
        assertThat(q.getComments().size(), lessThan(commentResultSize + 1));
        for (int i = 0; i < commentResultSize; i++) {
            assertThat(q.getComments().get(i).getAuthor(), notNullValue());
            assertThat(q.getComments().get(i).getAuthor().getUsername(), notNullValue());
            assertThat(q.getComments().get(i).getId(), notNullValue());
            assertThat(q.getComments().get(i).getText(), notNullValue());
            assertThat(q.getComments().get(i).getCreationDate(), notNullValue());
        }
    }

    @Test
    void getFullQuestion_NotFound() {
        createQuestionWithCommentAndAnswer();
        assertThat(questionDao.getFullQuestion(123432L), equalTo(null));
    }

    @Test
    void getQuestionCommentsPages_AssertCorrectData() throws NoSuchFieldException, IllegalAccessException {
        Field commentResultSizeField = QuestionDao.class.getDeclaredField("commentResultSize");
        commentResultSizeField.setAccessible(true);
        int commentResultSize = (int) commentResultSizeField.get(questionDao);

        createQuestionWithCommentAndAnswer();
        for (int i = 0; i < 15 / commentResultSize; i++) {
            List<CommentQuestion> comments = questionDao.getQuestionComments(1L, i);
            for (int y = 0; y < commentResultSize; y++) {
                assertThat(comments, notNullValue());
                assertThat(comments.get(y), notNullValue());
                assertThat(comments.get(y).getId(), notNullValue());
                assertThat(comments.get(y).getText(), notNullValue());
                assertThat(comments.get(y).getCreationDate(), notNullValue());
                assertThat(comments.get(y).getAuthor(), notNullValue());
            }
        }
    }

    @Test
    void getQuestionCommentsPages_AssertNoDuplicates() {
        createQuestionWithCommentAndAnswer();

        List<CommentQuestion> comments1 = questionDao.getQuestionComments(1L, 0);
        List<CommentQuestion> comments2 = questionDao.getQuestionComments(1L, 1);
        assertThat(comments1, notNullValue());
        assertThat(comments2, notNullValue());
        assertThat(comments1.size(), equalTo(comments2.size()));

        int size = comments1.size();
        long[] ids = new long[size * 2];
        for (int i = 0; i < size; i++) {
            ids[i] = comments1.get(i).getId();
        }
        for (int i = size; i < size * 2; i++) {
            ids[i] = comments2.get(i - size).getId();
        }
        assertThat(ids.length, equalTo(Arrays.stream(ids).distinct().toArray().length));
    }

    @Test
    void getQuestionCommentsPages_NotFound() {
        createQuestionWithCommentAndAnswer();
        assertThat(questionDao.getQuestionComments(123L, 0), equalTo(null));
        assertThat(questionDao.getQuestionComments(1L, 234234), equalTo(null));
    }

    @Test
    void getQuestionAnswersPages_AssertCorrectData() throws NoSuchFieldException, IllegalAccessException {
        Field resultSizeField = QuestionDao.class.getDeclaredField("resultSize");
        resultSizeField.setAccessible(true);
        int resultSize = (int) resultSizeField.get(questionDao);

        createQuestionWithCommentAndAnswer();

        for (int i = 0; i < 15 / resultSize; i++) {
            List<Answer> answers = questionDao.getQuestionAnswer(1L, i);
            assertThat(answers, notNullValue());
            for (Answer a : answers) {
                assertThat(a, notNullValue());
                assertThat(a.getId(), notNullValue());
                assertThat(a.getText(), notNullValue());
                assertThat(a.getAnswered(), notNullValue());
                assertThat(a.getCreationDate(), notNullValue());

                assertThat(a.getAuthor(), notNullValue());
                assertThat(a.getAuthor().getUsername(), notNullValue());

                assertThat(a.getComments(), notNullValue());
                for (CommentAnswer ca : a.getComments()) {
                    assertThat(ca, notNullValue());
                    assertThat(ca.getId(), notNullValue());
                    assertThat(ca.getText(), notNullValue());
                    assertThat(ca.getCreationDate(), notNullValue());

                    assertThat(ca.getAuthor(), notNullValue());
                    assertThat(ca.getAuthor().getUsername(), notNullValue());
                }
            }
        }
    }

    @Test
    void getQuestionAnswersPages_AssertNoDuplicates() {
        createQuestionWithCommentAndAnswer();

        List<Answer> answers1 = questionDao.getQuestionAnswer(1L, 0);
        List<Answer> answers2 = questionDao.getQuestionAnswer(1L, 1);
        assertThat(answers1, notNullValue());
        assertThat(answers2, notNullValue());
        assertThat(answers1.size(), equalTo(answers2.size()));

        int size = answers1.size();

        long[] ids = new long[size * 2];
        for (int i = 0; i < size; i++) {
            ids[i] = answers1.get(i).getId();
        }
        for (int i = size; i < size * 2; i++) {
            ids[i] = answers2.get(i - size).getId();
        }
        assertThat(ids.length, equalTo(Arrays.stream(ids).distinct().toArray().length));
    }

    @Test
    void getQuestionAnswersPages_NotFound() {
        createQuestionWithCommentAndAnswer();
        List<Answer> answers1 = questionDao.getQuestionAnswer(1L, 123123);
        List<Answer> answers2 = questionDao.getQuestionAnswer(1123123L, 1);
        assertThat(answers1, equalTo(null));
        assertThat(answers2, equalTo(null));
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
                    values (1, :date, :date, 'tags', 'text', 'title', 1)
                    """;
            String createAnswerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (:id, false, :date, 'text', 1, 1)
                    """;
            String createQuestionCommentSql =
                    """
                    insert into comment (comment_type, id, text, author_id, answer_id, creation_date, question_id)\s\
                    values ('question', :id, 'text', 1, null, :date, 1)
                    """;
            String createAnswerCommentSql =
                    """
                    insert into comment (comment_type, id, text, author_id, answer_id, creation_date, question_id)\s\
                    values ('answer', :id, 'text', 1, :answerId, :date, null)
                    """;
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery(createUserSql).executeUpdate();
            session.createSQLQuery(createQuestionSql)
                    .setParameter("date", new Date(648912731231L))
                    .executeUpdate();

            long commentId = 0;
            for (int i = 0; i < 15; i++) {
                session.createSQLQuery(createAnswerSql)
                        .setParameter("id", (long) i)
                        .setParameter("date", new Date(i * 123456788L))
                        .executeUpdate();
                session.createSQLQuery(createQuestionCommentSql)
                        .setParameter("id", commentId)
                        .setParameter("date", new Date(i * 123456789L))
                        .executeUpdate();
                commentId++;
                for (int y = 0; y < 20; y++) {
                    session.createSQLQuery(createAnswerCommentSql)
                            .setParameter("answerId", i)
                            .setParameter("id", commentId)
                            .setParameter("date", new Date(y + 100000000L))
                            .executeUpdate();
                    commentId++;
                }
                session.flush();
            }
            transaction.commit();
        }
    }
}
