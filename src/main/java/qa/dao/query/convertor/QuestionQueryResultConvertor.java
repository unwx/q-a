package qa.dao.query.convertor;

import qa.domain.CommentQuestion;
import qa.domain.Question;
import qa.domain.QuestionView;
import qa.dto.internal.hibernate.question.QuestionCommentDto;
import qa.dto.internal.hibernate.question.QuestionViewDto;
import qa.dto.internal.hibernate.question.QuestionWithCommentsDto;

import java.util.ArrayList;
import java.util.List;

public class QuestionQueryResultConvertor {

    private QuestionQueryResultConvertor() {}

    public static List<QuestionView> dtoToQuestionViewList(List<QuestionViewDto> dto) {
        final List<QuestionView> views = new ArrayList<>(dto.size());
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
                .comments(dtoToCommentQuestionList(dto.getComments()))
                .build();
    }

    private static QuestionView dtoToQuestionView(QuestionViewDto dto) {
        return new QuestionView(
                dto.getQuestionId(),
                dto.getTitle(),
                dto.getTags(),
                dto.getCreationDate(),
                dto.getLastActivity(),
                dto.getAnswersCount(),
                UserQueryResultConvertor.usernameToAuthor(dto.getAuthor().getUsername()));
    }

    private static List<CommentQuestion> dtoToCommentQuestionList(List<QuestionCommentDto> dto) {
        final List<CommentQuestion> commentQuestions = new ArrayList<>(dto.size());
        dto.forEach((d) -> commentQuestions.add(dtoToCommentQuestion(d)));
        return commentQuestions;
    }

    private static CommentQuestion dtoToCommentQuestion(QuestionCommentDto dto) {
        final CommentQuestion commentQuestion = new CommentQuestion();
        commentQuestion.setId(dto.getCommentId());
        commentQuestion.setText(dto.getText());
        commentQuestion.setCreationDate(dto.getCreationDate());
        commentQuestion.setAuthor(UserQueryResultConvertor.usernameToAuthor(dto.getAuthor().getUsername()));
        return commentQuestion;
    }
}
