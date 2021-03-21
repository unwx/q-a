package qa.domain;

import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.dao.databasecomponents.FieldExtractor;
import qa.domain.setters.SetterField;

import javax.persistence.*;

@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="comment_type", discriminatorType = DiscriminatorType.STRING)
public class Comment implements FieldExtractor, FieldDataSetterExtractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", length = 500, nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

    public Comment(Long id,
                   String text,
                   User author) {
        this.id = id;
        this.text = text;
        this.author = author;
    }

    public Comment(String text,
                   User author) {
        this.text = text;
        this.author = author;
    }

    public Comment() {
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public SetterField[] extractSettersField() {
        return new SetterField[]{
                new SetterField("id", Long.class),
                new SetterField("text", String.class),
                new SetterField("author", User.class),
        };
    }

    @Override
    public Field[] extract() {
        return new Field[]{
                new Field("id", id),
                new Field("text", text),
                new Field("author", author),
        };
    }

    public static class Builder {

        private final Comment comment;

        public Builder() {
            comment = new Comment();
        }

        public Builder id(Long id) {
            comment.id = id;
            return this;
        }

        public Builder text(String text) {
            comment.text = text;
            return this;
        }

        public Builder author(User author) {
            comment.author = author;
            return this;
        }

        public Comment build() {
            return comment;
        }
    }
}
