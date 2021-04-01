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
import qa.dto.internal.hibernate.question.QuestionAnswerCommentDto;
import qa.dto.internal.hibernate.question.QuestionAnswerDto;
import qa.dto.internal.hibernate.question.QuestionCommentDto;
import qa.dto.internal.hibernate.question.QuestionFullDto;
import qa.dto.internal.hibernate.transformer.QuestionFullDtoTransformer;
import qa.util.hibernate.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("deprecation")
public class QuestionDao extends DaoImpl<Question> {

    private final SessionFactory sessionFactory;
    private static final int resultSize = 6;
    private static final int commentResultSize = 3;

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
                        c.answer_id,\s\
                        u.username,\s\
                        ROW_NUMBER() OVER (PARTITION BY c.answer_id ORDER BY c.creation_date DESC) rn\s\
                    FROM comment AS c\s\
                    INNER JOIN usr u ON u.id = c.author_id)\s\
                SELECT\s\
                    q.title AS q_title, q.text AS q_text,\s\
                    q.tags AS q_tags, q.creation_date AS q_c_date,\s\
                    q.last_activity AS q_l_activity,\s\
                    
                    c.id AS q_c_id, c.text AS q_c_text, c.creation_date AS q_c_c_date,\s\
                    u.username AS q_a_username,\s\
                    cu.username AS q_c_a_username,\s\
                    
                    answ.id AS q_a_id, answ.text AS q_a_text,\s\
                    answ.answered AS q_a_answered, answ.creation_date as q_a_c_date,\s\
                    answ.username AS q_a_a_username,\s\
                    
                    comm.id AS q_a_c_id, comm.text AS q_a_c_text,\s\
                    comm.creation_date AS q_a_c_c_date, comm.username AS q_a_c_a_username\s\
                FROM question AS q\s\
                INNER JOIN comment AS c ON q.id = c.question_id\s\
                INNER JOIN usr AS u ON q.author_id = u.id\s\
                INNER JOIN usr AS cu ON c.author_id = cu.id\s\
                INNER JOIN answ ON q.id = answ.question_id AND answ.rn <= :answerLimit\s\
                INNER JOIN comm ON answ.id = comm.answer_id AND comm.rn <= :commentLimit\s\
                WHERE q.id = :questionId\s\
                ORDER BY c.creation_date desc\
                """;
        return session.createSQLQuery(getQuestionSql)
                .unwrap(Query.class)
                .setParameter("questionId", questionId)
                .setParameter("answerLimit", resultSize)
                .setParameter("commentLimit", commentResultSize)
                .setMaxResults(commentResultSize)
                .setResultTransformer(new QuestionFullDtoTransformer());
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

    private List<Answer> convertDtoToAnswerList(List<QuestionAnswerDto> dto) {
        List<Answer> answers = new ArrayList<>(dto.size());
        dto.forEach((d) -> answers.add(convertDtoToAnswer(d)));
        return answers;
    }

    private List<CommentQuestion> convertDtoToCommentQuestionList(List<QuestionCommentDto> dto) {
        List<CommentQuestion> commentQuestions = new ArrayList<>(dto.size());
        dto.forEach((d) -> commentQuestions.add(convertDtoToCommentQuestion(d)));
        return commentQuestions;
    }

    private List<CommentAnswer> convertDtoToCommentAnswerList(List<QuestionAnswerCommentDto> dto) {
        List<CommentAnswer> commentAnswers = new ArrayList<>(dto.size());
        dto.forEach((d) -> commentAnswers.add(convertDtoToCommentAnswer(d)));
        return commentAnswers;
    }

    private Answer convertDtoToAnswer(QuestionAnswerDto dto) {
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

    private CommentAnswer convertDtoToCommentAnswer(QuestionAnswerCommentDto dto) {
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
