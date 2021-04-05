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
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.util.QuestionDaoTestUtil;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.Arrays;
import java.util.Collections;
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
    void getFullQuestion_AssertCorrectData() {
        QuestionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(sessionFactory, 6, 3);
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
            assertThat(a.getComments().size(), lessThan(QuestionDaoTestUtil.commentResultSize + 1));
            for (CommentAnswer c : a.getComments()) {
                assertThat(c.getId(), notNullValue());
                assertThat(c.getText(), notNullValue());
                assertThat(c.getCreationDate(), notNullValue());
                assertThat(c.getAuthor(), notNullValue());
                assertThat(c.getAuthor().getUsername(), notNullValue());
            }
        }

        assertThat(q.getComments(), notNullValue());
        assertThat(q.getComments().size(), lessThan(QuestionDaoTestUtil.commentResultSize + 1));
        for (int i = 0; i < q.getComments().size(); i++) {
            assertThat(q.getComments().get(i).getAuthor(), notNullValue());
            assertThat(q.getComments().get(i).getAuthor().getUsername(), notNullValue());
            assertThat(q.getComments().get(i).getId(), notNullValue());
            assertThat(q.getComments().get(i).getText(), notNullValue());
            assertThat(q.getComments().get(i).getCreationDate(), notNullValue());
        }
    }

    @Test
    void getFullQuestion_AssertNoDuplicates() {
        QuestionDaoTestUtil.createQuestionWithCommentsAndAnswersWithComments(sessionFactory, 6, 3);
        Question q = questionDao.getFullQuestion(1L);

        assertThat(q, notNullValue());
        assertThat(q.getAnswers().size(), greaterThan(0));
        assertThat(q.getComments().size(), greaterThan(0));

        List<Answer> answers = q.getAnswers();
        List<CommentQuestion> comments = q.getComments();

        int answerSize = answers.size();
        int commentSize = comments.size();

        long[] answerIds = new long[answerSize];
        long[] commentIds = new long[commentSize];

        int answerCommentIdsSize =0;
        for (Answer answer : answers) {
            answerCommentIdsSize += answer.getComments().size();
        }

        long[] answerCommentIds = new long[answerCommentIdsSize];
        int answerCommentIdsIndex = 0;

        for (int i = 0; i < answerSize; i++) {
            answerIds[i] = answers.get(i).getId();
            for (int y = 0; y < answers.get(i).getComments().size(); y++) {
                List<CommentAnswer> commentAnswers = q.getAnswers().get(i).getComments();
                answerCommentIds[answerCommentIdsIndex] = commentAnswers.get(y).getId();
                answerCommentIdsIndex++;
            }
        }

        for (int i = 0; i < commentSize; i++) {
            commentIds[i] = comments.get(i).getId();
        }

        assertThat(answerIds, equalTo(Arrays.stream(answerIds).distinct().toArray()));
        assertThat(commentIds, equalTo(Arrays.stream(commentIds).distinct().toArray()));
        assertThat(answerCommentIds, equalTo(Arrays.stream(answerCommentIds).distinct().toArray()));
    }

    @Test
    void getFullQuestion_NotFound() {
        assertThat(questionDao.getFullQuestion(123432L), equalTo(null));
    }

    @Test
    void getFullQuestion_AssertNoNPE() {
        createQuestion();
        Question q = questionDao.getFullQuestion(1L);
        assertThat(q, notNullValue());
        assertThat(q.getAnswers(), notNullValue());
        assertThat(q.getComments(), notNullValue());
    }

    @Test
    void getFullQuestion_AssertNoNPE1() {
        createAnswer();
        Question q = questionDao.getFullQuestion(1L);
        assertThat(q, notNullValue());
        assertThat(q.getAnswers(), notNullValue());
        assertThat(q.getComments(), notNullValue());
    }

    @Test
    void getQuestionCommentsPages_AssertCorrectData() {
        QuestionDaoTestUtil.createQuestionWithComments(sessionFactory, (int) (QuestionDaoTestUtil.commentResultSize * 1.5));
        for (int i = 0; i < 2; i++) {
            List<CommentQuestion> comments = questionDao.getQuestionComments(1L, i);
            for (int y = 0; y < comments.size(); y++) {
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
        QuestionDaoTestUtil.createQuestionWithComments(sessionFactory, (int) (QuestionDaoTestUtil.commentResultSize * 1.5));

        List<CommentQuestion> comments1 = questionDao.getQuestionComments(1L, 0);
        List<CommentQuestion> comments2 = questionDao.getQuestionComments(1L, 1);
        assertThat(comments1, notNullValue());
        assertThat(comments2, notNullValue());
        assertThat(comments1.size(), equalTo(comments2.size()));

        int size1 = comments1.size();
        int size2 = comments1.size();
        long[] ids1 = new long[size1];
        long[] ids2 = new long[size2];
        for (int i = 0; i < size1; i++) {
            ids1[i] = comments1.get(i).getId();
        }
        for (int i = 0; i < size2; i++) {
            ids2[i] = comments2.get(i).getId();
        }
        assertThat(ids1, equalTo(Arrays.stream(ids1).distinct().toArray()));
        assertThat(ids2, equalTo(Arrays.stream(ids2).distinct().toArray()));
    }

    @Test
    void getQuestionCommentsPages_NotFound() {
        assertThat(questionDao.getQuestionComments(123L, 0), equalTo(Collections.emptyList()));
        assertThat(questionDao.getQuestionComments(1L, 234234), equalTo(Collections.emptyList()));
    }

    @Test
    void getQuestionAnswersPages_AssertCorrectData() {
        QuestionDaoTestUtil.createQuestionWithAnswersWithComments(
                sessionFactory,
                (int) (QuestionDaoTestUtil.resultSize * 1.5),
                QuestionDaoTestUtil.commentResultSize);

        for (int i = 0; i < 2; i++) {
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
        QuestionDaoTestUtil.createQuestionWithAnswersWithComments(
                sessionFactory,
                (int) (QuestionDaoTestUtil.resultSize * 1.5),
                QuestionDaoTestUtil.commentResultSize);

        List<Answer> answers1 = questionDao.getQuestionAnswer(1L, 0);
        List<Answer> answers2 = questionDao.getQuestionAnswer(1L, 1);
        assertThat(answers1, notNullValue());
        assertThat(answers2, notNullValue());

        int size1 = answers1.size();
        int size2 = answers2.size();

        long[] ids1 = new long[size1];
        long[] ids2 = new long[size2];
        for (int i = 0; i < size1; i++) {
            ids1[i] = answers1.get(i).getId();
        }
        for (int i = 0; i < size2; i++) {
            ids2[i] = answers2.get(i).getId();
        }
        assertThat(ids1, equalTo(Arrays.stream(ids1).distinct().toArray()));
        assertThat(ids2, equalTo(Arrays.stream(ids2).distinct().toArray()));
    }

    @Test
    void getQuestionAnswersPages_NotFound() {
        List<Answer> answers1 = questionDao.getQuestionAnswer(1L, 123123);
        List<Answer> answers2 = questionDao.getQuestionAnswer(1123123L, 1);
        assertThat(answers1, equalTo(Collections.emptyList()));
        assertThat(answers2, equalTo(Collections.emptyList()));
    }

    @Test
    void getQuestionAnswersPages_AssertNoNPE() {
        createQuestion();
        List<Answer> a = questionDao.getQuestionAnswer(1L, 1);
        assertThat(a, equalTo(Collections.emptyList()));
    }

    @Test
    void getQuestionAnswersPages_AssertNoNPE1() {
        createAnswer();
        List<Answer> a1 = questionDao.getQuestionAnswer(1L, 0);
        assertThat(a1, notNullValue());
        assertThat(a1.size(), equalTo(1));
    }

    @Test
    void getQuestionViewsPages_AssertCorrectData() {
        QuestionDaoTestUtil.createManyQuestionsWithManyAnswers(
                sessionFactory,
                (int) (QuestionDaoTestUtil.questionViewResultSize * 1.5),
                QuestionDaoTestUtil.resultSize);
        for (int i = 0; i < 2; i++) {
            List<QuestionViewDto> views = questionDao.getQuestionViewsDto(i);
            assertThat(views, notNullValue());
            for (QuestionViewDto v : views) {
                assertThat(v.getQuestionId(), notNullValue());
                assertThat(v.getTags(), notNullValue());
                assertThat(v.getTitle(), notNullValue());
                assertThat(v.getCreationDate(), notNullValue());
                assertThat(v.getLastActivity(), notNullValue());
                assertThat(v.getAuthor(), notNullValue());
                assertThat(v.getAuthor().getUsername(), notNullValue());
                assertThat(v.getAnswersCount(), notNullValue());
            }
        }
    }

    @Test
    void getQuestionViewsPages_AssertNoDuplicates() {
        QuestionDaoTestUtil.createManyQuestionsWithManyAnswers(
                sessionFactory,
                (int) (QuestionDaoTestUtil.questionViewResultSize * 1.5),
                QuestionDaoTestUtil.resultSize);

        List<QuestionViewDto> dto1 = questionDao.getQuestionViewsDto(0);
        List<QuestionViewDto> dto2 = questionDao.getQuestionViewsDto(1);
        assertThat(dto1, notNullValue());
        assertThat(dto2, notNullValue());
        int size1 = dto1.size();
        int size2 = dto2.size();
        long[] ids1 = new long[size1];
        long[] ids2 = new long[size2];
        for (int i = 0; i < size1; i++) {
            ids1[i] = dto1.get(i).getQuestionId();
        }
        for (int i = 0; i < size2; i++) {
            ids2[i] = dto2.get(i).getQuestionId();
        }
        assertThat(ids1, equalTo(Arrays.stream(ids1).distinct().toArray()));
        assertThat(ids2, equalTo(Arrays.stream(ids2).distinct().toArray()));
    }

    @Test
    void getQuestionViewsPages_NotFound() {
        List<QuestionViewDto> dto1 = questionDao.getQuestionViewsDto(1231230);
        assertThat(dto1, equalTo(Collections.emptyList()));
    }

    private void createQuestion() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String createUserSql =
                    """
                    insert into usr (id, about, username) values (1, null, 'username')\
                    """;
            String createQuestionSql =
                    """
                    insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                    values (1, '2021-04-05', '2021-04-05', 'tags', 'text', 'title', 1)\
                    """;
            session.createSQLQuery(createUserSql).executeUpdate();
            session.createSQLQuery(createQuestionSql).executeUpdate();
            transaction.commit();
        }
    }

    private void createAnswer() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            String createUserSql =
                    """
                    insert into usr (id, about, username) values (1, null, 'username')\
                    """;
            String createQuestionSql =
                    """
                     insert into question (id, creation_date, last_activity, tags, text, title, author_id)\s\
                     values (1, '2021-04-05', '2021-04-05', 'tags', 'text', 'title', 1)\
                     """;
            String createAnswerSql =
                    """
                    insert into answer (id, answered, creation_date, text, author_id, question_id)\s\
                    values (1, false, '2021-04-05', 'text', 1, 1)\
                    """;
            session.createSQLQuery(createUserSql).executeUpdate();
            session.createSQLQuery(createQuestionSql).executeUpdate();
            session.createSQLQuery(createAnswerSql).executeUpdate();

            transaction.commit();
        }
    }
}
