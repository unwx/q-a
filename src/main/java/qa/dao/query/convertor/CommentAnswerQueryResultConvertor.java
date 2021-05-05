package qa.dao.query.convertor;

import qa.domain.CommentAnswer;
import qa.dto.internal.hibernate.entities.comment.answer.CommentAnswerDto;

import java.util.ArrayList;
import java.util.List;

public class CommentAnswerQueryResultConvertor {

    private CommentAnswerQueryResultConvertor() {}

    public static List<CommentAnswer> dtoToCommentAnswerList(List<CommentAnswerDto> dto) {
        final List<CommentAnswer> commentAnswers = new ArrayList<>(dto.size());
        dto.forEach((d) -> commentAnswers.add(dtoToCommentAnswer(d)));
        return commentAnswers;
    }

    private static CommentAnswer dtoToCommentAnswer(CommentAnswerDto dto) {
        final CommentAnswer commentAnswer = new CommentAnswer();
        commentAnswer.setId(dto.getCommentId());
        commentAnswer.setText(dto.getText());
        commentAnswer.setCreationDate(dto.getCreationDate());
        commentAnswer.setAuthor(UserQueryResultConvertor.usernameToAuthor(dto.getAuthor().getUsername()));
        return commentAnswer;
    }
}
