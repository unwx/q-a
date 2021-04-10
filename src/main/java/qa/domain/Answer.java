package qa.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.dao.databasecomponents.FieldExtractor;
import qa.domain.setters.SetterField;
import qa.util.access.HasAuthor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table
public class Answer implements FieldExtractor, FieldDataSetterExtractor, HasAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", length = 2000, nullable = false)
    private String text;

    @Column(name = "answered", nullable = false)
    private Boolean answered;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private Date creationDate;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class, cascade = {
            CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH
    })
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Question.class, cascade = {
            CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH
    })
    @JoinColumn(name = "question_id", nullable = false, updatable = false)
    private Question question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CommentAnswer> comments;

    public Answer(Long id,
                  String text,
                  Boolean answered,
                  Date creationDate,
                  User author,
                  Question question,
                  List<CommentAnswer> comments) {
        this.id = id;
        this.text = text;
        this.answered = answered;
        this.author = author;
        this.question = question;
        this.comments = comments;
        this.creationDate = creationDate;
    }

    public Answer(String text,
                  Boolean answered,
                  Date creationDate,
                  User author,
                  Question question,
                  List<CommentAnswer> comments) {
        this.text = text;
        this.answered = answered;
        this.author = author;
        this.question = question;
        this.comments = comments;
        this.creationDate = creationDate;
    }

    public Answer(Long id) {
        this.id = id;
    }

    public Answer() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getAnswered() {
        return answered;
    }

    @JsonProperty("creation_date")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }

    @Override
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<CommentAnswer> getComments() {
        return comments;
    }

    public void setComments(List<CommentAnswer> comments) {
        this.comments = comments;
    }

    @Override
    public SetterField[] extractSettersField() {
        return new SetterField[]{
                new SetterField("id", Long.class),
                new SetterField("text", String.class),
                new SetterField("creationDate", Date.class),
                new SetterField("answered", Boolean.class),
                new SetterField("author", User.class),
                new SetterField("question", Question.class),
        };
    }

    @Override
    public Field[] extract() {
        return new Field[]{
                new Field("id", id),
                new Field("text", text),
                new Field("creationDate", creationDate),
                new Field("answered", answered),
                new Field("author", author),
                new Field("question", question),
        };
    }

    public static class Builder {

        private final Answer answer;

        public Builder() {
            answer = new Answer();
        }

        public Builder id(Long id) {
            answer.id = id;
            return this;
        }

        public Builder text(String text) {
            answer.text = text;
            return this;
        }

        public Builder answered(Boolean answered) {
            answer.answered = answered;
            return this;
        }

        public Builder creationDate(Date creationDate) {
            answer.creationDate = creationDate;
            return this;
        }

        public Builder author(User author) {
            answer.author = author;
            return this;
        }

        public Builder question(Question question) {
            answer.question = question;
            return this;
        }

        public Builder comments(List<CommentAnswer> comments) {
            answer.comments = comments;
            return this;
        }

        public Answer build() {
            return answer;
        }
    }
}
