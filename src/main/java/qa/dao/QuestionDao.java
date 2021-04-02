package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.domain.*;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.answer.AnswerCommentDto;
import qa.dto.internal.hibernate.answer.AnswerFullDto;
import qa.dto.internal.hibernate.question.QuestionCommentDto;
import qa.dto.internal.hibernate.question.QuestionFullDto;
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.dto.internal.hibernate.transformer.question.QuestionAnswerDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionCommentDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionFullDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionViewDtoTransformer;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("deprecation")
public class QuestionDao extends DaoImpl<Question> {

    private final SessionFactory sessionFactory;
    private static final int resultSize = 6;
    private static final int questionViewResultSize = 20;
    private static final int commentResultSize = 3;

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
            QuestionFullDto questionResult = getFullQuestionQuery(session, questionId).uniqueResult();
            if (questionResult == null) {
                transaction.rollback();
                return null;
            }

            transaction.commit();
            return convertDtoToQuestion(questionResult, questionId);
        }
    }

    @Nullable
    public List<QuestionViewDto> getQuestionViewsDto(int page) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<QuestionViewDto> views = getQuestionsViewsQuery(session, page).list();
            if (views.isEmpty()) {
                transaction.rollback();
                return null;
            }
            return views;
        }
    }

    @Nullable
    public List<CommentQuestion> getQuestionComments(Long questionId, int page) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<QuestionCommentDto> commentsResult = getQuestionCommentsQuery(session, questionId, page).list();
            if (commentsResult.isEmpty()) {
                transaction.rollback();
                return null;
            }
            transaction.commit();
            return convertDtoToCommentQuestionList(commentsResult);
        }
    }

    @Nullable
    public List<Answer> getQuestionAnswer(Long questionId, int page) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<AnswerFullDto> answerResult = getQuestionAnswersQuery(session, questionId, page).list();
            if (answerResult.isEmpty()) {
                transaction.rollback();
                return null;
            }
            transaction.commit();
            return convertDtoToAnswerList(answerResult);
        }
    }

    @SuppressWarnings("unchecked")
    private Query<QuestionFullDto> getFullQuestionQuery(Session session, Long questionId) {
        String getQuestionSql =
                """
                WITH\s\
                 answ AS (\
                    SELECT\s\
                        a.id,\s\
                        a.text,\s\
                        a.answered,\s\
                        a.creation_date,\s\
                        a.question_id,\s\
                        u.username,\s\
                        ROW_NUMBER() OVER (PARTITION BY a.question_id ORDER BY a.creation_date DESC) rn\s\
                    FROM answer AS a\s\
                    INNER JOIN usr u ON u.id = a.author_id),\s\
                 comm AS (\
                    SELECT\s\
                        c.id,\s\
                        c.text,\s\
                        c.creation_date,\s\
                        c.comment_type,\s\
                        c.answer_id,\s\
                        u.username,\s\
                        ROW_NUMBER() OVER (PARTITION BY c.answer_id ORDER BY c.creation_date DESC) rn\s\
                    FROM comment AS c\s\
                    INNER JOIN usr u ON u.id = c.author_id)\s\
                SELECT\s\
                    q.title AS que_title, q.text AS que_text,\s\
                    q.tags AS que_tags, q.creation_date AS que_c_date,\s\
                    q.last_activity AS que_l_activity,\s\
                    
                    c.id AS que_c_id, c.text AS que_c_text, c.creation_date AS que_c_c_date,\s\
                    u.username AS que_u_username,\s\
                    cu.username AS que_c_u_username,\s\
                    
                    answ.id AS ans_id, answ.text AS ans_text,\s\
                    answ.answered AS ans_answered, answ.creation_date AS ans_c_date,\s\
                    answ.username AS ans_u_username,\s\
                    
                    comm.id AS ans_c_id, comm.text AS ans_c_text,\s\
                    comm.creation_date AS ans_c_c_date, comm.username AS ans_c_u_username\s\
                FROM question AS q\s\
                INNER JOIN comment AS c ON q.id = c.question_id\s\
                INNER JOIN usr AS u ON q.author_id = u.id\s\
                INNER JOIN usr AS cu ON c.author_id = cu.id\s\
                INNER JOIN answ ON q.id = answ.question_id AND answ.rn <= :answerRN\s\
                INNER JOIN comm ON answ.id = comm.answer_id AND comm.rn <= :commentRN\s\
                WHERE q.id = :questionId AND c.comment_type = 'question' AND comm.comment_type = 'answer'\s\
                ORDER BY c.creation_date desc\
                """;
        return session.createSQLQuery(getQuestionSql)
                .unwrap(Query.class)
                .setParameter(QUESTION_ID_PARAMETER, questionId)
                .setParameter("answerRN", resultSize)
                .setParameter("commentRN", commentResultSize)
                .setMaxResults(commentResultSize)
                .setResultTransformer(new QuestionFullDtoTransformer());
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
                .setFirstResult(questionViewResultSize * page)
                .setMaxResults(questionViewResultSize)
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
                .setFirstResult(page * commentResultSize)
                .setMaxResults(commentResultSize)
                .setResultTransformer(new QuestionCommentDtoTransformer());
    }

    @SuppressWarnings("unchecked")
    private Query<AnswerFullDto> getQuestionAnswersQuery(Session session, Long questionId, int page) {
        String getQuestionAnswersSql =
                """
                WITH
                 answ AS (\
                    SELECT\s\
                        a.id,\s\
                        a.text,\s\
                        a.answered,\s\
                        a.creation_date,\s\
                        a.question_id,\s\
                        u.username,\s\
                        ROW_NUMBER() OVER (PARTITION BY question_id ORDER BY creation_date DESC) rn\s\
                    FROM answer AS a\s\
                    INNER JOIN usr u ON a.author_id = u.id\s\
                    OFFSET :offset),\s\
                 comm AS (\
                    SELECT\s\
                        c.id,\s\
                        c.text,\s\
                        c.creation_date,\s\
                        c.answer_id,\s\
                        c.comment_type,\s\
                        u.username,\s\
                        ROW_NUMBER() OVER (PARTITION BY c.answer_id ORDER BY c.creation_date DESC) rn\s\
                    FROM comment AS c\s\
                    INNER JOIN usr AS u ON u.id = c.author_id)\s\
                SELECT\s\
                    answ.id AS ans_id, answ.text AS ans_text,\s\
                    answ.answered AS ans_answered, answ.creation_date as ans_c_date,\s\
                    answ.username AS ans_u_username,\s\
                    
                    comm.id AS ans_c_id, comm.text AS ans_c_text,\s\
                    comm.creation_date AS ans_c_c_date, comm.username AS ans_c_u_username\s\
                FROM question AS q\s\
                INNER JOIN answ ON q.id = answ.question_id AND answ.rn <= :answerRN\s\
                INNER JOIN comm ON answ.id = comm.answer_id AND comm.rn <= :commentRN\s\
                WHERE q.id = :questionId AND comm.comment_type = 'answer'\s\
                """;
        return session.createSQLQuery(getQuestionAnswersSql)
                .unwrap(Query.class)
                .setParameter(QUESTION_ID_PARAMETER, questionId)
                .setParameter("answerRN", resultSize + page * resultSize)
                .setParameter("commentRN", commentResultSize)
                .setParameter("offset", page * resultSize)
                .setResultTransformer(new QuestionAnswerDtoTransformer());
    }

    private Question convertDtoToQuestion(QuestionFullDto dto, Long questionId) {
        return new Question.Builder()
                .id(questionId)
                .title(dto.getTitle())
                .text(dto.getText())
                .tags(dto.getTags())
                .creationDate(dto.getCreationDate())
                .lastActivity(dto.getLastActivity())
                .author(convertUsernameToAuthor(dto.getAuthor().getUsername()))
                .answers(convertDtoToAnswerList(dto.getAnswers()))
                .comments(convertDtoToCommentQuestionList(dto.getComments()))
                .build();
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
