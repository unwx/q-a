package qa.dao.query;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import qa.dao.query.convertor.UserResultConvertor;
import qa.dao.query.parameters.CommentQueryParameters;
import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.dto.internal.hibernate.answer.AnswerCommentDto;
import qa.dto.internal.hibernate.answer.AnswerFullDto;
import qa.dto.internal.hibernate.transformer.answer.AnswerCommentDtoResultTransformer;
import qa.dto.internal.hibernate.transformer.question.QuestionAnswerFullDtoTransformer;

import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings({"deprecation", "unchecked"})
public class AnswerQueryFactory {

    private static final int RESULT_SIZE = 6;

    private final ResultConvertor resultConvertor = new ResultConvertor();

    public Query<AnswerFullDto> answersWithCommentsQuery(Session session, Long questionId) {
        return answersWithCommentsQuery(session, questionId, 0);
    }

    public Query<AnswerFullDto> answersWithCommentsQuery(Session session, Long questionId, int page) {
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
                .setParameter("commentLimit", CommentQueryParameters.COMMENT_RESULT_SIZE)
                .setParameter("answerLimit", RESULT_SIZE)
                .setParameter("offset", RESULT_SIZE * page)
                .setResultTransformer(new QuestionAnswerFullDtoTransformer());
    }

    public Query<AnswerCommentDto> answerCommentsQuery(Session session, Long answerId, int page) {
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

    public ResultConvertor getConvertor() {
        return resultConvertor;
    }

    public static class ResultConvertor extends UserResultConvertor {

        public List<Answer> dtoToAnswerList(List<AnswerFullDto> dto) {
            List<Answer> answers = new ArrayList<>(dto.size());
            dto.forEach((d) -> answers.add(dtoToAnswer(d)));
            return answers;
        }

        public List<CommentAnswer> dtoToCommentAnswerList(List<AnswerCommentDto> dto) {
            List<CommentAnswer> commentAnswers = new ArrayList<>(dto.size());
            dto.forEach((d) -> commentAnswers.add(dtoToCommentAnswer(d)));
            return commentAnswers;
        }

        public Answer dtoToAnswer(AnswerFullDto dto) {
            return new Answer.Builder()
                    .id(dto.getAnswerId())
                    .text(dto.getText())
                    .answered(dto.getAnswered())
                    .creationDate(dto.getCreationDate())
                    .author(usernameToAuthor(dto.getAuthor().getUsername()))
                    .comments(dtoToCommentAnswerList(dto.getComments()))
                    .build();
        }

        public CommentAnswer dtoToCommentAnswer(AnswerCommentDto dto) {
            CommentAnswer commentAnswer = new CommentAnswer();
            commentAnswer.setId(dto.getCommentId());
            commentAnswer.setText(dto.getText());
            commentAnswer.setCreationDate(dto.getCreationDate());
            commentAnswer.setAuthor(usernameToAuthor(dto.getAuthor().getUsername()));
            return commentAnswer;
        }
    }
}
