package qa.dao.query;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.parameters.CommentQueryParameters;
import qa.dto.internal.hibernate.question.QuestionFullStringIdsDto;
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.dto.internal.hibernate.question.QuestionWithCommentsDto;
import qa.dto.internal.hibernate.transformer.question.QuestionFullIdsDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionViewDtoTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionWithCommentsDtoTransformer;

@SuppressWarnings({"deprecation", "unchecked"})
public class QuestionQueryCreator {

    private static final int QUESTION_VIEW_RESULT_SIZE = 20;

    private QuestionQueryCreator() {}

    public static Query<QuestionWithCommentsDto> questionWithCommentsQuery(Session session, long questionId) {
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
                .setParameter("questionId", questionId)
                .setParameter("commentLimit", CommentQueryParameters.COMMENT_RESULT_SIZE)
                .setResultTransformer(new QuestionWithCommentsDtoTransformer());
    }

    public static Query<QuestionViewDto> questionsViewsQuery(Session session, int page) {
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

    public static Query<QuestionFullStringIdsDto> questionFullIdsQuery(Session session, long questionId) {
                String getIdsSql =
                """
                SELECT\s\
                     a.id AS ans_id, c_a.id AS com_ans_id, c_q.id AS com_que_id\s\
                FROM question AS q\s\
                LEFT JOIN answer AS a ON q.id = a.question_id\s\
                LEFT JOIN comment AS c_a ON a.id = c_a.answer_id\s\
                LEFT JOIN comment AS c_q ON q.id = c_q.question_id\s\
                WHERE q.id = :questionId\
                """;
        return session.createSQLQuery(getIdsSql)
                .unwrap(Query.class)
                .setParameter("questionId", questionId)
                .setResultTransformer(new QuestionFullIdsDtoTransformer());
    }
}
