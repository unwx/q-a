package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.*;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.answer.AnswerCommentDto;
import qa.dto.internal.hibernate.answer.AnswerFullDto;
import qa.dto.internal.hibernate.question.QuestionCommentDto;
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.dto.internal.hibernate.question.QuestionWithCommentsDto;
import qa.dto.internal.hibernate.transformer.question.QuestionAnswerFullDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionCommentDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionViewDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionWithCommentsDtoTransformer;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("deprecation")
public class QuestionDao extends DaoImpl<Question> {

    private final SessionFactory sessionFactory;
    private static final int RESULT_SIZE = 6;
    private static final int QUESTION_VIEW_RESULT_SIZE = 20;
    private static final int COMMENT_RESULT_SIZE = 3;

    private static final String QUESTION_ID_PARAMETER = "questionId";

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
        try(Session session = sessionFactory.openSession()) {
            Query<?> query = session.createQuery("select id from Question where id=:a").setParameter("a", id);
            Transaction transaction = session.beginTransaction();
            Object result = query.uniqueResult();
            transaction.commit();
            return result != null;
        }
    }

    @Nullable
    public Question getFullQuestion(Long questionId) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            QuestionWithCommentsDto questionResult = getQuestionWithCommentsQuery(session, questionId).uniqueResult();
            if (questionResult == null) {
                transaction.rollback();
                return null;
            }
            List<Answer> answers = convertDtoToAnswerList(getQuestionAnswersWithCommentsQuery(session, questionId).list());
            transaction.commit();
            Question question = convertDtoToQuestion(questionResult, questionId);
            question.setAnswers(answers);
            return question;
        }
    }

    @NotNull
    public List<QuestionViewDto> getQuestionViewsDto(int page) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<QuestionViewDto> views = getQuestionsViewsQuery(session, page).list();
            transaction.commit();
            return views;
        }
    }

    @NotNull
    public List<CommentQuestion> getQuestionComments(Long questionId, int page) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<QuestionCommentDto> commentsResult = getQuestionCommentsQuery(session, questionId, page).list();
            transaction.commit();
            return convertDtoToCommentQuestionList(commentsResult);
        }
    }

    @NotNull
    public List<Answer> getQuestionAnswer(Long questionId, int page) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<AnswerFullDto> answerResult = getQuestionAnswersWithCommentsQuery(session, questionId, page).list();
            transaction.commit();
            return convertDtoToAnswerList(answerResult);
        }
    }

    @SuppressWarnings("unchecked")
    private Query<QuestionWithCommentsDto> getQuestionWithCommentsQuery(Session session, Long questionId) {
        String sql =
                """
                 SELECT\s\
                     ques.title AS que_title, ques.text AS que_text,\s\
                     ques.tags AS que_tags, ques.creation_date AS que_c_date,\s\
                     ques.last_activity AS que_l_activity,\s\
                     
                     ques_comm.id AS que_c_id, ques_comm.text AS que_c_text, ques_comm.creation_date AS que_c_c_date,\s\
                     ques_comm.username AS que_c_u_username,\s\
                     
                     u.username AS que_u_username\s\
                 FROM question AS ques\s\
                 LEFT JOIN LATERAL\s\
                     (\
                     SELECT c.id, c.text, c.creation_date, c.question_id, c.author_id, u.username\s\
                     FROM comment AS c\s\
                     INNER JOIN usr AS u ON c.author_id = u.id\s\
                     WHERE c.question_id = ques.id\s\
                     ORDER BY c.creation_date\s\
                     LIMIT :commentRN\s\
                     ) AS ques_comm ON TRUE\s\
                 INNER JOIN usr AS u ON ques.author_id = u.id\s\
                 WHERE ques.id = :questionId\
                 """;
        return session.createSQLQuery(sql)
                .unwrap(Query.class)
                .setParameter(QUESTION_ID_PARAMETER, questionId)
                .setParameter("commentRN", COMMENT_RESULT_SIZE)
                .setResultTransformer(new QuestionWithCommentsDtoTransformer());
    }

    private Query<AnswerFullDto> getQuestionAnswersWithCommentsQuery(Session session, Long questionId) {
        return getQuestionAnswersWithCommentsQuery(session, questionId, 0);
    }

    @SuppressWarnings("unchecked")
    private Query<AnswerFullDto> getQuestionAnswersWithCommentsQuery(Session session, Long questionId, int page) {
        String sql =
                """
                SELECT\s\
                    answ.id AS ans_id, answ.text AS ans_text,\s\
                    answ.answered AS ans_answered, answ.creation_date AS ans_c_date,\s\
                    answ.username AS ans_u_username,\s\

                    answ_comm.id AS ans_c_id, answ_comm.text AS ans_c_text,\s\
                    answ_comm.creation_date AS ans_c_c_date, answ_comm.username AS ans_c_u_username\s\
                FROM question AS ques\s\
                LEFT JOIN LATERAL\s\
                    (\
                    SELECT a.id, a.text, a.answered, a.creation_date, a.question_id, a.author_id, u.username\s\
                    FROM answer AS a\s\
                    INNER JOIN usr AS u ON a.author_id = u.id\s\
                    WHERE a.question_id = ques.id\s\
                    ORDER BY a.creation_date\s\
                    LIMIT :answerLimit OFFSET :offset\s\
                    ) AS answ ON TRUE\s\
                LEFT JOIN LATERAL \s\
                    (\
                    SELECT c.id, c.text, c.creation_date, c.answer_id, c.author_id, u.username\s\
                    FROM comment AS c\s\
                    INNER JOIN usr AS u ON c.author_id = u.id\s\
                    WHERE c.answer_id = answ.id\s\
                    ORDER BY c.creation_date\s\
                    LIMIT :commentLimit\s\
                    ) AS answ_comm ON TRUE\s\
                WHERE ques.id = :questionId\
                """;
        return session.createSQLQuery(sql)
                .unwrap(Query.class)
                .setParameter(QUESTION_ID_PARAMETER, questionId)
                .setParameter("commentLimit", COMMENT_RESULT_SIZE)
                .setParameter("answerLimit", RESULT_SIZE)
                .setParameter("offset", RESULT_SIZE * page)
                .setResultTransformer(new QuestionAnswerFullDtoTransformer());
    }

    @SuppressWarnings("unchecked")
    private Query<QuestionViewDto> getQuestionsViewsQuery(Session session, int page) {
        String getQuestionViewsSql =
                """
                SELECT q.id AS que_id, q.title AS que_title, q.tags AS que_tags,\s\
                q.creation_date AS que_c_date, q.last_activity AS que_l_activity,\s\
                a.count AS que_a_count,\s\
                u.username AS que_u_username\s\
                FROM question AS q\s\
                INNER JOIN (\
                    SELECT a.question_id,\s\
                    COUNT(a.id) AS count FROM answer AS a\s\
                    GROUP BY a.question_id) AS a ON q.id = a.question_id\s\
                INNER JOIN usr AS u ON q.author_id = u.id\s\
                ORDER BY q.creation_date DESC\
                """;
        return session.createSQLQuery(getQuestionViewsSql)
                .unwrap(Query.class)
                .setFirstResult(QUESTION_VIEW_RESULT_SIZE * page)
                .setMaxResults(QUESTION_VIEW_RESULT_SIZE)
                .setResultTransformer(new QuestionViewDtoTransformer());
    }

    @SuppressWarnings("unchecked")
    private Query<QuestionCommentDto> getQuestionCommentsQuery(Session session, Long questionId, int page) {
        String getQuestionCommentsSql =
                """
                SELECT c.id AS que_c_id, c.text AS que_c_text, c.creation_date AS que_c_c_date,\s\
                u.username AS que_c_u_username\s\
                FROM comment AS c\s\
                INNER JOIN usr AS u ON c.author_id = u.id\s\
                INNER JOIN question AS q ON c.question_id = q.id\s\
                WHERE q.id = :questionId AND c.comment_type = 'question'\s\
                ORDER BY c.creation_date DESC\
                """;
        return session.createSQLQuery(getQuestionCommentsSql)
                .unwrap(Query.class)
                .setParameter(QUESTION_ID_PARAMETER, questionId)
                .setFirstResult(page * COMMENT_RESULT_SIZE)
                .setMaxResults(COMMENT_RESULT_SIZE)
                .setResultTransformer(new QuestionCommentDtoTransformer());
    }

    private List<Answer> convertDtoToAnswerList(List<AnswerFullDto> dto) {
        List<Answer> answers = new ArrayList<>(dto.size());
        dto.forEach((d) -> answers.add(convertDtoToAnswer(d)));
        return answers;
    }

    private List<CommentQuestion> convertDtoToCommentQuestionList(List<QuestionCommentDto> dto) {
        List<CommentQuestion> commentQuestions = new ArrayList<>(dto.size());
        dto.forEach((d) -> commentQuestions.add(convertDtoToCommentQuestion(d)));
        return commentQuestions;
    }

    private List<CommentAnswer> convertDtoToCommentAnswerList(List<AnswerCommentDto> dto) {
        List<CommentAnswer> commentAnswers = new ArrayList<>(dto.size());
        dto.forEach((d) -> commentAnswers.add(convertDtoToCommentAnswer(d)));
        return commentAnswers;
    }

    private Question convertDtoToQuestion(QuestionWithCommentsDto dto, Long questionId) {
        return new Question.Builder()
                .id(questionId)
                .title(dto.getTitle())
                .text(dto.getText())
                .tags(dto.getTags())
                .creationDate(dto.getCreationDate())
                .lastActivity(dto.getLastActivity())
                .author(convertUsernameToAuthor(dto.getAuthor().getUsername()))
                .comments(convertDtoToCommentQuestionList(dto.getComments()))
                .build();
    }

    private Answer convertDtoToAnswer(AnswerFullDto dto) {
        return new Answer.Builder()
                .id(dto.getAnswerId())
                .text(dto.getText())
                .answered(dto.getAnswered())
                .creationDate(dto.getCreationDate())
                .author(convertUsernameToAuthor(dto.getAuthor().getUsername()))
                .comments(convertDtoToCommentAnswerList(dto.getComments()))
                .build();
    }

    private CommentQuestion convertDtoToCommentQuestion(QuestionCommentDto dto) {
        CommentQuestion commentQuestion = new CommentQuestion();
        commentQuestion.setId(dto.getCommentId());
        commentQuestion.setText(dto.getText());
        commentQuestion.setCreationDate(dto.getCreationDate());
        commentQuestion.setAuthor(convertUsernameToAuthor(dto.getAuthor().getUsername()));
        return commentQuestion;
    }

    private CommentAnswer convertDtoToCommentAnswer(AnswerCommentDto dto) {
        CommentAnswer commentAnswer = new CommentAnswer();
        commentAnswer.setId(dto.getCommentId());
        commentAnswer.setText(dto.getText());
        commentAnswer.setCreationDate(dto.getCreationDate());
        commentAnswer.setAuthor(convertUsernameToAuthor(dto.getAuthor().getUsername()));
        return commentAnswer;
    }

    private User convertUsernameToAuthor(String username) {
        return new User.Builder().username(username).build();
    }
}
