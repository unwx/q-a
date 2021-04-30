package qa.dao.query.convertor;

import qa.domain.CommentQuestion;
import qa.dto.internal.hibernate.comment.question.CommentQuestionDto;

import java.util.ArrayList;
import java.util.List;

public class CommentQuestionQueryResultConvertor {

    private CommentQuestionQueryResultConvertor() {}

    public static List<CommentQuestion> dtoToCommentQuestionList(List<CommentQuestionDto> dto) {
        final List<CommentQuestion> commentQuestions = new ArrayList<>(dto.size());
        dto.forEach((d) -> commentQuestions.add(dtoToCommentQuestion(d)));
        return commentQuestions;
    }

    private static CommentQuestion dtoToCommentQuestion(CommentQuestionDto dto) {
        final CommentQuestion commentQuestion = new CommentQuestion();
        commentQuestion.setId(dto.getCommentId());
        commentQuestion.setText(dto.getText());
        commentQuestion.setCreationDate(dto.getCreationDate());
        commentQuestion.setAuthor(UserQueryResultConvertor.usernameToAuthor(dto.getAuthor().getUsername()));
        return commentQuestion;
    }
}
