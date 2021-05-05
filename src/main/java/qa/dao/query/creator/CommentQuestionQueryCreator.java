package qa.dao.query.creator;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.parameters.QueryParameter;
import qa.dto.internal.hibernate.entities.comment.question.CommentQuestionDto;
import qa.dto.internal.hibernate.transformer.comment.CommentQuestionDtoResultTransformer;

@SuppressWarnings({"deprecation", "unchecked"})
public class CommentQuestionQueryCreator {

    private CommentQuestionQueryCreator() {}

    public static Query<CommentQuestionDto> commentsQuery(Session session, long questionId, int page) {
        final String sql =
                """
                SELECT\s\
                    c.id AS c_id, c.text AS c_text, c.creation_date AS c_c_date,\s\
                    c.username AS c_u_username\s\
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
        return session.createSQLQuery(sql)
                .unwrap(Query.class)
                .setParameter("questionId", questionId)
                .setParameter("limit", QueryParameter.COMMENT_RESULT_SIZE)
                .setParameter("offset", page * QueryParameter.COMMENT_RESULT_SIZE)
                .setResultTransformer(new CommentQuestionDtoResultTransformer());
    }
}