package qa.dao.query.creator;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.parameters.QueryParameter;
import qa.dto.internal.hibernate.entities.comment.answer.CommentAnswerDto;
import qa.dto.internal.hibernate.transformer.comment.CommentAnswerDtoResultTransformer;

@SuppressWarnings({"deprecation", "unchecked"})
public class CommentAnswerQueryCreator {

    private CommentAnswerQueryCreator() {}

    public static Query<CommentAnswerDto> commentsQuery(Session session, long answerId, int page) {
        final String sql =
                """
                SELECT\s\
                    c.id AS c_id, c.creation_date AS c_c_date, c.text AS c_text,\s\
                    c.username as c_u_username\s\
                FROM answer AS a\s\
                LEFT JOIN LATERAL\s\
                    (\
                    SELECT c.id, c.creation_date, c.text, u.username\s\
                    FROM comment AS c\s\
                    INNER JOIN usr u ON c.author_id = u.id\s\
                    ORDER BY c.creation_date\s\
                    LIMIT :limit OFFSET :offset\s\
                    ) AS c ON TRUE\s\
                WHERE a.id = :answerId\
                """;
        return session.createSQLQuery(sql)
                .unwrap(Query.class)
                .setParameter("answerId", answerId)
                .setParameter("limit", QueryParameter.COMMENT_RESULT_SIZE)
                .setParameter("offset", QueryParameter.COMMENT_RESULT_SIZE * page)
                .setResultTransformer(new CommentAnswerDtoResultTransformer());
    }
}
