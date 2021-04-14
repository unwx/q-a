package qa.dao.query.convertor;

import qa.domain.Question;
import qa.domain.QuestionView;
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.dto.internal.hibernate.question.QuestionWithCommentsDto;

import java.util.ArrayList;
import java.util.List;

public class QuestionQueryResultConvertor {

    private QuestionQueryResultConvertor() {
    }

    public static List<QuestionView> dtoToQuestionViewList(List<QuestionViewDto> dto) {
        List<QuestionView> views = new ArrayList<>(dto.size());
        dto.forEach((d) -> views.add(dtoToQuestionView(d)));
        return views;
    }

    public static Question dtoToQuestion(QuestionWithCommentsDto dto, Long questionId) {
        return new Question.Builder()
                .id(questionId)
                .title(dto.getTitle())
                .text(dto.getText())
                .tags(dto.getTags())
                .creationDate(dto.getCreationDate())
                .lastActivity(dto.getLastActivity())
                .author(UserQueryResultConvertor.usernameToAuthor(dto.getAuthor().getUsername()))
                .comments(CommentQuestionQueryResultConvertor.dtoToCommentQuestionList(dto.getComments()))
                .build();
    }

    public static QuestionView dtoToQuestionView(QuestionViewDto dto) {
        return new QuestionView(
                dto.getQuestionId(),
                dto.getTitle(),
                dto.getTags(),
                dto.getCreationDate(),
                dto.getLastActivity(),
                dto.getAnswersCount(),
                UserQueryResultConvertor.usernameToAuthor(dto.getAuthor().getUsername()));
    }
}
