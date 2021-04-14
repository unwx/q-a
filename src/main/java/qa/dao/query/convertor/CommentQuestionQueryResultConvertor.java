package qa.dao.query.convertor;

import qa.domain.CommentQuestion;
import qa.dto.internal.hibernate.question.QuestionCommentDto;

import java.util.ArrayList;
import java.util.List;

public class CommentQuestionQueryResultConvertor {

    private CommentQuestionQueryResultConvertor() {
    }

    public static List<CommentQuestion> dtoToCommentQuestionList(List<QuestionCommentDto> dto) {
        List<CommentQuestion> commentQuestions = new ArrayList<>(dto.size());
        dto.forEach((d) -> commentQuestions.add(dtoToCommentQuestion(d)));
        return commentQuestions;
    }

    public static CommentQuestion dtoToCommentQuestion(QuestionCommentDto dto) {
        CommentQuestion commentQuestion = new CommentQuestion();
        commentQuestion.setId(dto.getCommentId());
        commentQuestion.setText(dto.getText());
        commentQuestion.setCreationDate(dto.getCreationDate());
        commentQuestion.setAuthor(UserQueryResultConvertor.usernameToAuthor(dto.getAuthor().getUsername()));
        return commentQuestion;
    }
}
