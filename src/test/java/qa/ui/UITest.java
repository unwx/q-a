package qa.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.domain.CommentQuestion;
import qa.dto.response.JwtPairResponse;
import qa.dto.response.question.QuestionFullResponse;
import qa.dto.response.question.QuestionViewResponse;
import qa.logger.TestLogger;
import qa.security.PasswordEncryptorFactory;
import qa.security.jwt.service.JwtProvider;
import qa.tools.annotations.SpringTest;
import qa.util.dao.query.builder.QueryBuilder;
import qa.util.dao.query.builder.redis.RedisQueryBuilder;
import qa.util.rest.JwtTestUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringTest
public class UITest {

    private static String accessToken;
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JedisResourceCenter jedisCenter;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncryptorFactory passwordEncryptorFactory;

    private final TestLogger logger = new TestLogger(UITest.class);

    @BeforeAll
    void init() throws JsonProcessingException {
        RestAssured.baseURI = "http://localhost:8080/api/v1/";
        RestAssured.port = 8080;

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createSQLQuery("truncate table question cascade").executeUpdate();
            session.createSQLQuery("truncate table answer cascade").executeUpdate();
            session.createSQLQuery("truncate table comment cascade").executeUpdate();
            session.createSQLQuery("truncate table authentication cascade").executeUpdate();
            session.createSQLQuery("truncate table usr cascade").executeUpdate();
            transaction.commit();
        }
        JedisResource resource = jedisCenter.getResource();
        resource.getJedis().flushDB();
        resource.close();

        final QueryBuilder queryBuilder = new QueryBuilder(sessionFactory);
        final RedisQueryBuilder redisQueryBuilder = new RedisQueryBuilder(jedisCenter);
        final UITestUtil util = new UITestUtil(queryBuilder, redisQueryBuilder);
        JwtTestUtil.createUserWithRefreshTokenAndEncryptedPassword(sessionFactory, jwtProvider, passwordEncryptorFactory.create());

        final String body = UIRequests.authorize().post(UIRequestExecutor.LOGIN).body().asString();
        final ObjectMapper mapper = new ObjectMapper();
        final JwtPairResponse tokens = mapper.readValue(body, JwtPairResponse.class);

        accessToken = tokens.getAccess();
        util.prepare();
    }

    @Test
    void check_questions_click_one() throws JsonProcessingException {
        logger.trace("check question and click one");
        final RequestSpecification request = UIRequests.getRequest();
        final QuestionViewResponse[] views = UIRequestExecutor.getViews(request);

        final int viewTarget = (int) (Math.random() * views.length);
        final long questionId = views[viewTarget].getQuestionId();

        final QuestionFullResponse question = UIRequestExecutor.getQuestion(request, questionId);
        assertThat(question.getQuestionId(), equalTo(questionId));
    }

    @Test
    void check_question_like_it_leave_comment() throws JsonProcessingException {
        logger.trace("check question, like it and leave comment");
        final RequestSpecification request = UIRequests.getRequest();
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);

        final QuestionFullResponse question = UIRequestExecutor.getQuestion(request);
        final long questionId = question.getQuestionId();

        final RequestSpecification likeRequest = UIRequests.questionLike(questionId, accessToken);
        UIRequestExecutor.likeQuestion(likeRequest);

        final RequestSpecification createCommentRequest = UIRequests.createCommentQuestion(questionId, accessToken);
        UIRequestExecutor.createCommentQuestion(createCommentRequest);

        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, questionId);
        final int questionLikes = question.getLikes();
        final int questionCommentsCount = question.getComments().size();
        final int updateQuestionLikes = updatedQuestion.getLikes();
        final int updatedQuestionCommentsCount = updatedQuestion.getComments().size();
        final boolean updatedQuestionLiked = updatedQuestion.isLiked();

        assertThat(updateQuestionLikes, greaterThan(questionLikes));
        assertThat(updatedQuestionCommentsCount, greaterThan(questionCommentsCount));
        assertThat(updatedQuestionLiked, equalTo(true));
    }

    @Test
    void like_answer_leave_comment() throws JsonProcessingException {
        logger.trace("like answer, leave comment");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);

        QuestionFullResponse question = null;
        List<Answer> answers = new ArrayList<>();

        while (answers.isEmpty()) {
            question = UIRequestExecutor.getQuestion(authenticatedRequest);
            answers = question.getAnswers();
        }

        final Answer answer = answers.get(0);
        final long answerId = answer.getId();
        final RequestSpecification likeRequest = UIRequests.answerLike(answerId, accessToken);
        final RequestSpecification createCommentAnswerRequest = UIRequests.createCommentAnswer(answerId, accessToken);

        UIRequestExecutor.likeAnswer(likeRequest);
        UIRequestExecutor.createCommentAnswer(createCommentAnswerRequest);
        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, question.getQuestionId());
        final Answer updatedAnswer = updatedQuestion.getAnswers().get(0);
        final long updatedAnswerId = updatedAnswer.getId();

        final int answerLikes = answer.getLikes();
        final int commentsCount = answer.getComments().size();

        final int updatedAnswerLikes = updatedAnswer.getLikes();
        final boolean updatedAnswerLiked = updatedAnswer.isLiked();
        final int updatedCommentsCount = updatedAnswer.getComments().size();

        assertThat(updatedAnswerId, equalTo(answerId));
        assertThat(updatedAnswerLikes, greaterThan(answerLikes));
        assertThat(updatedAnswerLiked, equalTo(true));
        assertThat(updatedCommentsCount, greaterThan(commentsCount));
    }

    @Test
    void create_question_answer_it_set_answered_remove_answered() throws JsonProcessingException {
        /*
         * the first, previously authenticated user creates a question,
         * the second created user creates an answer,
         * the first sets the "answered" status to the answer
         * then remove it. (answered)
         */
        logger.trace("create question, answer it, set answered, remove answered");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);
        final String secondUserToken = JwtTestUtil.createSecondUserWithToken(sessionFactory, jwtProvider);

        final RequestSpecification createQuestionRequest = UIRequests.createQuestion(accessToken);
        final long createdQuestionId = UIRequestExecutor.createQuestion(createQuestionRequest);

        final RequestSpecification createAnswerRequest = UIRequests.createAnswer(createdQuestionId, secondUserToken);
        final long createdAnswerId = UIRequestExecutor.createAnswer(createAnswerRequest);

        final RequestSpecification answeredRequest = UIRequests.setAnswered(createdAnswerId, accessToken);
        UIRequestExecutor.setAnswered(answeredRequest);

        final QuestionFullResponse question = UIRequestExecutor.getQuestion(authenticatedRequest, createdQuestionId);
        final List<Answer> answers = question.getAnswers();
        assertThat(answers.isEmpty(), equalTo(false));

        final Answer answer = answers.get(0);
        assertThat(answer.getAnswered(), equalTo(true));

        UIRequestExecutor.removeAnswered(answeredRequest);
        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, createdQuestionId);
        final List<Answer> updatedAnswers = updatedQuestion.getAnswers();

        final Answer updatedAnswer = updatedAnswers.get(0);
        assertThat(updatedAnswer.getAnswered(), equalTo(false));
    }

    @Test
    void create_delete_question() {
        logger.trace("create question, then delete");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);

        final RequestSpecification createQuestionRequest = UIRequests.createQuestion(accessToken);
        final long createdQuestionId = UIRequestExecutor.createQuestion(createQuestionRequest);

        final RequestSpecification deleteQuestionRequest = UIRequests.deleteQuestion(createdQuestionId, accessToken);
        UIRequestExecutor.deleteQuestion(deleteQuestionRequest);

        final int status = authenticatedRequest.get(UIRequestExecutor.QUESTION_FULL.formatted(createdQuestionId)).statusCode();
        assertThat(status, equalTo(404));
    }

    @Test
    void create_update_question() throws JsonProcessingException {
        logger.trace("create question, then update");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);

        final RequestSpecification createQuestionRequest = UIRequests.createQuestion(accessToken);
        final long createdQuestionId = UIRequestExecutor.createQuestion(createQuestionRequest);

        final QuestionFullResponse createdQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, createdQuestionId);

        final RequestSpecification editQuestionRequest = UIRequests.editQuestion(createdQuestionId, accessToken);
        UIRequestExecutor.editQuestion(editQuestionRequest);

        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, createdQuestionId);
        final long questionId = createdQuestion.getQuestionId();
        final String questionText = createdQuestion.getText();
        final long updatedQuestionId = updatedQuestion.getQuestionId();
        final String updatedQuestionText = updatedQuestion.getText();

        assertThat(questionId, equalTo(updatedQuestionId));
        assertThat(questionText, not(updatedQuestionText));
    }

    @Test
    void create_delete_answer() throws JsonProcessingException {
        logger.trace("create answer, then delete");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);
        final QuestionViewResponse[] views = UIRequestExecutor.getViews(authenticatedRequest);
        final long questionId = views[0].getQuestionId();

        final RequestSpecification createAnswerRequest = UIRequests.createAnswer(questionId, accessToken);
        final long answerId = UIRequestExecutor.createAnswer(createAnswerRequest);

        final QuestionFullResponse question = UIRequestExecutor.getQuestion(authenticatedRequest, questionId);
        final int originalAnswerSize = question.getAnswers().size();

        final RequestSpecification deleteAnswerRequest = UIRequests.deleteAnswer(answerId, accessToken);
        UIRequestExecutor.deleteAnswer(deleteAnswerRequest);

        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, questionId);
        final int updatedAnswersCount = updatedQuestion.getAnswers().size();

        assertThat(originalAnswerSize, greaterThan(updatedAnswersCount));
    }

    @Test
    void create_update_answer() throws JsonProcessingException {
        logger.trace("create answer, then update");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);
        final QuestionViewResponse[] views = UIRequestExecutor.getViews(authenticatedRequest);
        final long questionId = views[0].getQuestionId();

        final RequestSpecification createAnswerRequest = UIRequests.createAnswer(questionId, accessToken);
        final long answerId = UIRequestExecutor.createAnswer(createAnswerRequest);
        final QuestionFullResponse question = UIRequestExecutor.getQuestion(authenticatedRequest, questionId);
        final Answer answer = question
                .getAnswers()
                .stream()
                .filter((a) -> a.getId() == answerId)
                .findFirst()
                .orElseThrow();

        final RequestSpecification editAnswerRequest = UIRequests.editAnswer(answerId, accessToken);
        UIRequestExecutor.editAnswer(editAnswerRequest);

        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, questionId);
        updatedQuestion.getAnswers().forEach((a) -> assertThat(a.getText(), not(answer.getText())));
    }

    @Test
    void create_delete_comment_question() throws JsonProcessingException {
        logger.trace("create comment question, then delete");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);
        final QuestionViewResponse[] views = UIRequestExecutor.getViews(authenticatedRequest);
        final long questionId = views[0].getQuestionId();

        final RequestSpecification createCommentQuestionRequest = UIRequests.createCommentQuestion(questionId, accessToken);
        final long commentId = UIRequestExecutor.createCommentQuestion(createCommentQuestionRequest);

        final QuestionFullResponse question = UIRequestExecutor.getQuestion(authenticatedRequest, questionId);
        final int commentCount = question.getComments().size();

        final RequestSpecification deleteCommentRequest = UIRequests.deleteCommentQuestion(commentId, accessToken);
        UIRequestExecutor.deleteCommentQuestion(deleteCommentRequest);

        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, questionId);
        final int updatedCommentCount = updatedQuestion.getComments().size();
        assertThat(commentCount, greaterThan(updatedCommentCount));
    }

    @Test
    void create_update_comment_question() throws JsonProcessingException {
        logger.trace("create comment question, then update");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);
        final QuestionViewResponse[] views = UIRequestExecutor.getViews(authenticatedRequest);
        final long questionId = views[0].getQuestionId();

        final RequestSpecification createCommentQuestionRequest = UIRequests.createCommentQuestion(questionId, accessToken);
        final long commentId = UIRequestExecutor.createCommentQuestion(createCommentQuestionRequest);

        final QuestionFullResponse question = UIRequestExecutor.getQuestion(authenticatedRequest, questionId);
        final CommentQuestion createdComment =
                question
                        .getComments()
                        .stream()
                        .filter((c) -> c.getId() == commentId)
                        .findFirst()
                        .orElseThrow();

        final RequestSpecification editCommentQuestion = UIRequests.editCommentQuestion(commentId, accessToken);
        UIRequestExecutor.editCommentQuestion(editCommentQuestion);

        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, questionId);
        updatedQuestion.getComments().forEach((c) -> assertThat(c.getText(), not(createdComment.getText())));
    }

    @Test
    void create_delete_comment_answer() throws JsonProcessingException {
        logger.trace("create comment answer, then delete");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);

        QuestionFullResponse question = null;
        List<Answer> answers = new ArrayList<>();

        while (answers.isEmpty()) {
            question = UIRequestExecutor.getQuestion(authenticatedRequest);
            answers = question.getAnswers();
        }

        final Answer answer = answers.get(0);
        final long answerId = answer.getId();

        final RequestSpecification createCommentRequest = UIRequests.createCommentAnswer(answerId, accessToken);
        final long createdCommentId = UIRequestExecutor.createCommentAnswer(createCommentRequest);

        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, question.getQuestionId());
        final Answer updatedAnswer = updatedQuestion.getAnswers().get(0);
        final int commentAnswersCount = updatedAnswer.getComments().size();

        final RequestSpecification deleteCommentRequest = UIRequests.deleteCommentAnswer(createdCommentId, accessToken);
        UIRequestExecutor.deleteCommentAnswer(deleteCommentRequest);

        final QuestionFullResponse updatedQuestion_1 = UIRequestExecutor.getQuestion(authenticatedRequest, question.getQuestionId());
        final Answer updatedAnswer_1 = updatedQuestion_1.getAnswers().get(0);
        final int commentAnswersCount_1 = updatedAnswer_1.getComments().size();

        assertThat(commentAnswersCount, greaterThan(commentAnswersCount_1));
    }

    @Test
    void create_update_comment_answer() throws JsonProcessingException {
        logger.trace("create comment answer, then update");
        final RequestSpecification authenticatedRequest = UIRequests.getRequestJwt(accessToken);
        QuestionFullResponse question = null;
        List<Answer> answers = new ArrayList<>();

        while (answers.isEmpty()) {
            question = UIRequestExecutor.getQuestion(authenticatedRequest);
            answers = question.getAnswers();
        }

        final Answer answer = answers.get(0);
        final long answerId = answer.getId();

        final RequestSpecification createCommentRequest = UIRequests.createCommentAnswer(answerId, accessToken);
        final long createdCommentId = UIRequestExecutor.createCommentAnswer(createCommentRequest);

        final QuestionFullResponse updatedQuestion = UIRequestExecutor.getQuestion(authenticatedRequest, question.getQuestionId());

        final Answer updatedAnswer = updatedQuestion.getAnswers().get(0);
        final CommentAnswer createdComment =
                updatedAnswer
                        .getComments()
                        .stream()
                        .filter((c) -> c.getId() == createdCommentId)
                        .findFirst()
                        .orElseThrow();

        final RequestSpecification editCommentRequest = UIRequests.editCommentAnswer(createdCommentId, accessToken);
        UIRequestExecutor.editCommentAnswer(editCommentRequest);

        final QuestionFullResponse updatedQuestion_1 = UIRequestExecutor.getQuestion(authenticatedRequest, question.getQuestionId());
        final Answer updatedAnswer_1 = updatedQuestion_1.getAnswers().get(0);
        updatedAnswer_1.getComments().forEach((c) -> assertThat(c.getText(), not(createdComment.getText())));
    }
}
