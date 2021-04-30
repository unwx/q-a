package qa.dao.query.convertor;

import qa.domain.Answer;
import qa.domain.CommentAnswer;
import qa.dto.internal.hibernate.answer.AnswerCommentDto;
import qa.dto.internal.hibernate.answer.AnswerFullDto;

import java.util.ArrayList;
import java.util.List;

public class AnswerQueryResultConvertor {

    private AnswerQueryResultConvertor() {}

    public static List<Answer> dtoToAnswerList(List<AnswerFullDto> dto) {
        final List<Answer> answers = new ArrayList<>(dto.size());
        dto.forEach((d) -> answers.add(dtoToAnswer(d)));
        return answers;
    }

    public static Answer dtoToAnswer(AnswerFullDto dto) {
        return new Answer.Builder()
                .id(dto.getAnswerId())
                .text(dto.getText())
                .answered(dto.getAnswered())
                .creationDate(dto.getCreationDate())
                .author(UserQueryResultConvertor.usernameToAuthor(dto.getAuthor().getUsername()))
                .comments(dtoToCommentAnswers(dto.getComments()))
                .build();
    }

    private static List<CommentAnswer> dtoToCommentAnswers(List<AnswerCommentDto> dto) {
        final List<CommentAnswer> commentAnswers = new ArrayList<>(dto.size());
        dto.forEach((d) -> commentAnswers.add(dtoToCommentAnswer(d)));
        return commentAnswers;
    }

    private static CommentAnswer dtoToCommentAnswer(AnswerCommentDto dto) {
        final CommentAnswer commentAnswer = new CommentAnswer();
        commentAnswer.setId(dto.getCommentId());
        commentAnswer.setText(dto.getText());
        commentAnswer.setCreationDate(dto.getCreationDate());
        commentAnswer.setAuthor(UserQueryResultConvertor.usernameToAuthor(dto.getAuthor().getUsername()));
        return commentAnswer;
    }
}
