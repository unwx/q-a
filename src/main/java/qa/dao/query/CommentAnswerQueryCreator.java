package qa.dao.query;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.parameters.CommentQueryParameters;
import qa.dto.internal.hibernate.answer.AnswerCommentDto;
import qa.dto.internal.hibernate.transformer.answer.AnswerCommentDtoResultTransformer;

@SuppressWarnings({"deprecation", "unchecked"})
public class CommentAnswerQueryCreator {

    private CommentAnswerQueryCreator() {
    }

    public static Query<AnswerCommentDto> commentsQuery(Session session, long answerId, int page) {
        String sql =
                """
                SELECT\s\
                    c.id AS ans_c_id, c.creation_date as ans_c_c_date, c.text as ans_c_text,\s\
                    c.username as ans_c_u_username\s\
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
                .setParameter("limit", CommentQueryParameters.COMMENT_RESULT_SIZE)
                .setParameter("offset", CommentQueryParameters.COMMENT_RESULT_SIZE * page)
                .setResultTransformer(new AnswerCommentDtoResultTransformer());
    }
}
