package qa.dao.query.manager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.convertor.CommentAnswerQueryResultConvertor;
import qa.dao.query.creator.CommentAnswerQueryCreator;
import qa.domain.CommentAnswer;
import qa.dto.internal.hibernate.entities.comment.answer.CommentAnswerDto;

import java.util.List;

public class CommentAnswerQueryManager {

    private CommentAnswerQueryManager() {}

    public static Query<CommentAnswerDto> commentsQuery(Session session, long answerId, int page) {
        return CommentAnswerQueryCreator.commentsQuery(session, answerId, page);
    }

    public static List<CommentAnswer> dtoToCommentAnswerList(List<CommentAnswerDto> dto) {
        return CommentAnswerQueryResultConvertor.dtoToCommentAnswerList(dto);
    }
}
