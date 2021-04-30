package qa.dao.query;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.parameters.QueryParameter;
import qa.dto.internal.hibernate.answer.AnswerFullDto;
import qa.dto.internal.hibernate.answer.AnswerFullStringIdsDto;
import qa.dto.internal.hibernate.transformer.answer.AnswerFullIdsDtoResultTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionAnswerFullDtoTransformer;

@SuppressWarnings({"deprecation", "unchecked"})
public class AnswerQueryCreator {

    private static final int RESULT_SIZE = QueryParameter.ANSWER_RESULT_SIZE;

    private AnswerQueryCreator() {}

    public static Query<AnswerFullDto> answersWithCommentsQuery(Session session, long questionId) {
        return answersWithCommentsQuery(session, questionId, 0);
    }

    public static Query<AnswerFullDto> answersWithCommentsQuery(Session session, long questionId, int page) {
        final String sql =
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
                    SELECT a.id, a.text, a.answered, a.creation_date, a.author_id, u.username\s\
                    FROM answer AS a\s\
                    INNER JOIN usr AS u ON a.author_id = u.id\s\
                    WHERE a.question_id = ques.id\s\
                    ORDER BY a.creation_date\s\
                    LIMIT :answerLimit OFFSET :offset\s\
                    ) AS answ ON TRUE\s\
                LEFT JOIN LATERAL \s\
                    (\
                    SELECT c.id, c.text, c.creation_date, c.author_id, u.username\s\
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
                .setParameter("questionId", questionId)
                .setParameter("commentLimit", QueryParameter.COMMENT_RESULT_SIZE)
                .setParameter("answerLimit", RESULT_SIZE)
                .setParameter("offset", RESULT_SIZE * page)
                .setResultTransformer(new QuestionAnswerFullDtoTransformer());
    }

    public static Query<AnswerFullStringIdsDto> answerFullIdsQuery(Session session, long answerId) {
        final String getIdsSql =
                """
                SELECT\s\
                    c_a.id AS com_ans_id\s\
                FROM answer AS a\s\
                LEFT JOIN comment c_a ON a.id = c_a.answer_id\s\
                WHERE a.id = :answerId\
                """;
        return session.createSQLQuery(getIdsSql)
                .unwrap(Query.class)
                .setParameter("answerId", answerId)
                .setResultTransformer(new AnswerFullIdsDtoResultTransformer());

    }
}
