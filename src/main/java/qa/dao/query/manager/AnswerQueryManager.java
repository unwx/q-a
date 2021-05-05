package qa.dao.query.manager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.convertor.AnswerQueryResultConvertor;
import qa.dao.query.creator.AnswerQueryCreator;
import qa.domain.Answer;
import qa.dto.internal.hibernate.entities.answer.AnswerFullDto;
import qa.dto.internal.hibernate.entities.answer.AnswerFullStringIdsDto;

import java.util.List;

public class AnswerQueryManager {

    private AnswerQueryManager() {}

    public static Query<AnswerFullDto> answersWithCommentsQuery(Session session, long questionId) {
        return AnswerQueryCreator.answersWithCommentsQuery(session, questionId);
    }

    public static Query<AnswerFullDto> answersWithCommentsQuery(Session session, long questionId, int page) {
        return AnswerQueryCreator.answersWithCommentsQuery(session, questionId, page);
    }

    public static Query<AnswerFullStringIdsDto> answerFullIdsQuery(Session session, long answerId) {
        return AnswerQueryCreator.answerFullIdsQuery(session, answerId);
    }

    public static List<Answer> dtoToAnswerList(List<AnswerFullDto> dto) {
        return AnswerQueryResultConvertor.dtoToAnswerList(dto);
    }
}
