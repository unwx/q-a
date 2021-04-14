package qa.dao.query.convertor;

import qa.domain.Answer;
import qa.dto.internal.hibernate.answer.AnswerFullDto;

import java.util.ArrayList;
import java.util.List;

public class AnswerQueryResultConvertor {

    private AnswerQueryResultConvertor() {
    }

    public static List<Answer> dtoToAnswerList(List<AnswerFullDto> dto) {
        List<Answer> answers = new ArrayList<>(dto.size());
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
                .comments(CommentAnswerQueryResultConvertor.dtoToCommentAnswerList(dto.getComments()))
                .build();
    }
}
