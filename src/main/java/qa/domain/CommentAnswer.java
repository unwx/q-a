package qa.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import qa.dao.databasecomponents.Field;
import qa.domain.setters.SetterField;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue("answer")
public class CommentAnswer extends Comment {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "answer_id")
    private Answer answer;

    public CommentAnswer(String text,
                         User author,
                         Answer answer) {
        super(text, new Date(), author);
        this.answer = answer;
    }

    public CommentAnswer() {
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    @Override
    public SetterField[] extractSettersField() {
        return new SetterField[] {
                new SetterField("id", Long.class),
                new SetterField("text", String.class),
                new SetterField("author", User.class),
                new SetterField("answer", Answer.class),
        };
    }

    @Override
    public Field[] extract() {
        return new Field[] {
                new Field("id", id),
                new Field("text", text),
                new Field("author", author),
                new Field("answer", answer),
        };
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setLikes(int count) {

    }

    @Override
    public int getLikes() {
        return 0;
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    @Override
    public String getText() {
        return super.getText();
    }

    @Override
    public void setText(String text) {
        super.setText(text);
    }

    @Override
    public User getAuthor() {
        return super.getAuthor();
    }

    @Override
    public void setAuthor(User author) {
        super.setAuthor(author);
    }

    @Override
    @JsonProperty("creation_date")
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    public void setCreationDate(Date creationDate) {
        super.setCreationDate(creationDate);
    }

    @Override
    public void setLiked(boolean liked) {

    }

    @Override
    public boolean isLiked() {
        return false;
    }
}
