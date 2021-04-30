package qa.dao.query.manager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.QuestionQueryCreator;
import qa.dao.query.convertor.QuestionQueryResultConvertor;
import qa.domain.Question;
import qa.domain.QuestionView;
import qa.dto.internal.hibernate.question.QuestionFullStringIdsDto;
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.dto.internal.hibernate.question.QuestionWithCommentsDto;

import java.util.List;

public class QuestionQueryManager {

    private QuestionQueryManager() {}

    public static Query<QuestionWithCommentsDto> questionWithCommentsQuery(Session session, long questionId) {
        return QuestionQueryCreator.questionWithCommentsQuery(session, questionId);
    }

    public static Query<QuestionViewDto> questionsViewsQuery(Session session, int page) {
        return QuestionQueryCreator.questionsViewsQuery(session, page);
    }

    public static Query<QuestionFullStringIdsDto> questionFullIdsQuery(Session session, long questionId) {
        return QuestionQueryCreator.questionFullIdsQuery(session, questionId);
    }

    public static Query<Long> questionAuthorIdFromAnswerQuery(Session session, long answerId) {
        return QuestionQueryCreator.questionAuthorIdFromAnswerQuery(session, answerId);
    }

    public static List<QuestionView> dtoToQuestionViewList(List<QuestionViewDto> dto) {
        return QuestionQueryResultConvertor.dtoToQuestionViewList(dto);
    }

    public static Question dtoToQuestion(QuestionWithCommentsDto dto, Long questionId) {
        return QuestionQueryResultConvertor.dtoToQuestion(dto, questionId);
    }
}
