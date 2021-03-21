package qa.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.dao.databasecomponents.FieldExtractor;
import qa.domain.setters.SetterField;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class Answer implements FieldExtractor, FieldDataSetterExtractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", length = 2000, nullable = false)
    private String text;

    @Column(name = "adopted", nullable = false)
    private Boolean adopted;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class, cascade = {
            CascadeType.REFRESH, CascadeType.MERGE
    })
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Question.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id", nullable = false, updatable = false)
    private Question question;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "answer_comment_id") // TODO sever
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<AnswerComment> comments;

    public Answer(Long id,
                  String text,
                  Boolean adopted,
                  User author,
                  Question question,
                  List<AnswerComment> comments) {
        this.id = id;
        this.text = text;
        this.adopted = adopted;
        this.author = author;
        this.question = question;
        this.comments = comments;
    }

    public Answer(String text,
                  Boolean adopted,
                  User author,
                  Question question,
                  List<AnswerComment> comments) {
        this.text = text;
        this.adopted = adopted;
        this.author = author;
        this.question = question;
        this.comments = comments;
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

    public Boolean getAdopted() {
        return adopted;
    }

    public void setAdopted(Boolean adopted) {
        this.adopted = adopted;
    }

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

    public List<AnswerComment> getComments() {
        return comments;
    }

    public void setComments(List<AnswerComment> comments) {
        this.comments = comments;
    }

    @Override
    public SetterField[] extractSettersField() {
        return new SetterField[]{
                new SetterField("id", Long.class),
                new SetterField("text", String.class),
                new SetterField("adopted", Boolean.class),
                new SetterField("author", User.class),
                new SetterField("question", Question.class),
        };
    }

    @Override
    public Field[] extract() {
        return new Field[]{
                new Field("id", id),
                new Field("text", text),
                new Field("adopted", adopted),
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

        public Builder adopted(Boolean adopted) {
            answer.adopted = adopted;
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

        public Builder comments(List<AnswerComment> comments) {
            answer.comments = comments;
            return this;
        }

        public Answer build() {
            return answer;
        }
    }
}
