package qa.dao.query;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.parameters.CommentQueryParameters;
import qa.dto.internal.hibernate.question.QuestionCommentDto;
import qa.dto.internal.hibernate.transformer.question.QuestionCommentDtoTransformer;

@SuppressWarnings({"deprecation", "unchecked"})
public class CommentQuestionQueryCreator {

    private CommentQuestionQueryCreator() {
    }

    public static Query<QuestionCommentDto> commentsQuery(Session session, long questionId, int page) {
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
                .setParameter("questionId", questionId)
                .setParameter("limit", CommentQueryParameters.COMMENT_RESULT_SIZE)
                .setParameter("offset", page * CommentQueryParameters.COMMENT_RESULT_SIZE)
                .setResultTransformer(new QuestionCommentDtoTransformer());
    }
}