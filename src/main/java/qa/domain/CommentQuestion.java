package qa.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import qa.cache.Cached;
import qa.dao.databasecomponents.Field;
import qa.domain.setters.SetterField;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue("question")
public class CommentQuestion extends Comment {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "question_id")
    private Question question;

    @Cached
    @Transient
    private int likes;

    @Cached
    @Transient
    private boolean liked;

    public CommentQuestion(String text,
                           User author,
                           Question question) {
        super(text, new Date(), author);
        this.question = question;
    }

    public CommentQuestion(Long id, String text, Date creationDate, User author) {
        super(id, text, creationDate, author);
    }

    public CommentQuestion() {
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public SetterField[] extractSettersField() {
        return new SetterField[] {
                new SetterField("id", Long.class),
                new SetterField("text", String.class),
                new SetterField("author", User.class),
                new SetterField("question", Question.class),
        };
    }

    @Override
    public Field[] extract() {
        return new Field[] {
                new Field("id", id),
                new Field("text", text),
                new Field("author", author),
                new Field("question", question),
        };
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setLikes(int count) {
        this.likes = count;
    }

    @Override
    public int getLikes() {
        return this.likes;
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
        this.liked = liked;
    }

    @Override
    public boolean isLiked() {
        return this.liked;
    }
}
