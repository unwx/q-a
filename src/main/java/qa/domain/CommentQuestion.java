package qa.domain;

import qa.dao.database.components.Field;
import qa.domain.setters.SetterField;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue("question")
public class CommentQuestion extends Comment {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "question_id")
    private Question question;

    public CommentQuestion(String text,
                           User author,
                           Question question) {
        super(text, new Date(), author);
        this.question = question;
    }

    public CommentQuestion(Long id, String text, Date creationDate, User author) {
        super(id, text, creationDate, author);
    }

    public CommentQuestion() {}

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
}
