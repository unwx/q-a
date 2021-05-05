package qa.dao.query.manager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.convertor.CommentQuestionQueryResultConvertor;
import qa.dao.query.creator.CommentQuestionQueryCreator;
import qa.domain.CommentQuestion;
import qa.dto.internal.hibernate.entities.comment.question.CommentQuestionDto;

import java.util.List;

public class CommentQuestionQueryManager {

    private CommentQuestionQueryManager() {}

    public static Query<CommentQuestionDto> commentsQuery(Session session, long questionId, int page) {
        return CommentQuestionQueryCreator.commentsQuery(session, questionId, page);
    }

    public static List<CommentQuestion> dtoToCommentQuestionList(List<CommentQuestionDto> dto) {
        return CommentQuestionQueryResultConvertor.dtoToCommentQuestionList(dto);
    }
}
