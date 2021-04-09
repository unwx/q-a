package qa.dao.query;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import qa.dao.query.convertor.UserResultConvertor;
import qa.dao.query.parameters.CommentQueryParameters;
import qa.domain.CommentQuestion;
import qa.domain.Question;
import qa.domain.QuestionView;
import qa.dto.internal.hibernate.question.QuestionCommentDto;
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.dto.internal.hibernate.question.QuestionWithCommentsDto;
import qa.dto.internal.hibernate.transformer.question.QuestionCommentDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionViewDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionWithCommentsDtoTransformer;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings({"deprecation", "unchecked"})
public class QuestionQueryFactory {

    private static final int QUESTION_VIEW_RESULT_SIZE = 20;
    private static final String QUESTION_ID_PARAMETER = "questionId";

    private final ResultConvertor resultConvertor = new ResultConvertor();

    public Query<QuestionWithCommentsDto> questionWithCommentsQuery(Session session, long questionId) {
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
                     SELECT c.id, c.text, c.creation_date, c.author_id, u.username\s\
                     FROM comment AS c\s\
                     INNER JOIN usr AS u ON c.author_id = u.id\s\
                     WHERE c.question_id = ques.id\s\
                     ORDER BY c.creation_date\s\
                     LIMIT :commentLimit\s\
                     ) AS ques_comm ON TRUE\s\
                 INNER JOIN usr AS u ON ques.author_id = u.id\s\
                 WHERE ques.id = :questionId\
                 """;
        return session.createSQLQuery(sql)
                .unwrap(Query.class)
                .setParameter(QUESTION_ID_PARAMETER, questionId)
                .setParameter("commentLimit", CommentQueryParameters.COMMENT_RESULT_SIZE)
                .setResultTransformer(new QuestionWithCommentsDtoTransformer());
    }

    public Query<QuestionViewDto> questionsViewsQuery(Session session, int page) {
        String getQuestionViewsSql =
                """
                SELECT\s\
                    q.id AS que_id, q.title AS que_title, q.tags AS que_tags,\s\
                    q.creation_date AS que_c_date, q.last_activity AS que_l_activity,\s\
                    a.count AS que_a_count,\s\
                    u.username AS que_u_username\s\
                FROM question AS q\s\
                LEFT JOIN (\
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

    public Query<QuestionCommentDto> questionCommentsQuery(Session session, long questionId, int page) {
        String getQuestionCommentsSql =
                """
                SELECT\s\
                    c.id AS que_c_id, c.text AS que_c_text, c.creation_date AS que_c_c_date,\s\
                    c.username AS que_c_u_username\s\
                FROM question AS q\s\
                LEFT JOIN LATERAL\s\
                    (\
                    SELECT c.id, c.text, c.creation_date, u.username\s\
                    FROM comment AS c\s\
                    INNER JOIN usr AS u ON c.author_id = u.id\s\
                    WHERE c.question_id = q.id\s\
                    ORDER BY c.creation_date\s\
                    LIMIT :limit OFFSET :offset\s\
                    ) AS c ON TRUE\s\
                WHERE q.id = :questionId\
                """;
        return session.createSQLQuery(getQuestionCommentsSql)
                .unwrap(Query.class)
                .setParameter(QUESTION_ID_PARAMETER, questionId)
                .setParameter("limit", CommentQueryParameters.COMMENT_RESULT_SIZE)
                .setParameter("offset", page * CommentQueryParameters.COMMENT_RESULT_SIZE)
                .setResultTransformer(new QuestionCommentDtoTransformer());
    }

    public ResultConvertor getConvertor() {
        return resultConvertor;
    }

    public static class ResultConvertor extends UserResultConvertor {

        public List<CommentQuestion> dtoToCommentQuestionList(List<QuestionCommentDto> dto) {
            List<CommentQuestion> commentQuestions = new ArrayList<>(dto.size());
            dto.forEach((d) -> commentQuestions.add(dtoToCommentQuestion(d)));
            return commentQuestions;
        }

        public List<QuestionView> dtoToQuestionViewList(List<QuestionViewDto> dto) {
            List<QuestionView> views = new ArrayList<>(dto.size());
            dto.forEach((d) -> views.add(dtoToQuestionView(d)));
            return views;
        }

        public Question dtoToQuestion(QuestionWithCommentsDto dto, Long questionId) {
            return new Question.Builder()
                    .id(questionId)
                    .title(dto.getTitle())
                    .text(dto.getText())
                    .tags(dto.getTags())
                    .creationDate(dto.getCreationDate())
                    .lastActivity(dto.getLastActivity())
                    .author(usernameToAuthor(dto.getAuthor().getUsername()))
                    .comments(dtoToCommentQuestionList(dto.getComments()))
                    .build();
        }

        public CommentQuestion dtoToCommentQuestion(QuestionCommentDto dto) {
            CommentQuestion commentQuestion = new CommentQuestion();
            commentQuestion.setId(dto.getCommentId());
            commentQuestion.setText(dto.getText());
            commentQuestion.setCreationDate(dto.getCreationDate());
            commentQuestion.setAuthor(usernameToAuthor(dto.getAuthor().getUsername()));
            return commentQuestion;
        }

        public QuestionView dtoToQuestionView(QuestionViewDto dto) {
            return new QuestionView(
                    dto.getQuestionId(),
                    dto.getTitle(),
                    dto.getTags(),
                    dto.getCreationDate(),
                    dto.getLastActivity(),
                    dto.getAnswersCount(),
                    usernameToAuthor(dto.getAuthor().getUsername()));
        }
    }
}
