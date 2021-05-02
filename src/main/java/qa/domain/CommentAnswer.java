package qa.domain;

import qa.dao.database.components.Field;
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

    public CommentAnswer() {}

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
}
