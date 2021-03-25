package qa.domain;

import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.dao.databasecomponents.FieldExtractor;
import qa.util.access.HasAuthor;

import javax.persistence.*;

@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="comment_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Comment implements FieldExtractor, FieldDataSetterExtractor, HasAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "text", length = 500, nullable = false)
    protected String text;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class, cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    protected User author;

    protected Comment(Long id,
                   String text,
                   User author) {
        this.id = id;
        this.text = text;
        this.author = author;
    }

    protected Comment(String text,
                   User author) {
        this.text = text;
        this.author = author;
    }

    protected Comment() {
    }


    protected Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    protected String getText() {
        return text;
    }

    protected void setText(String text) {
        this.text = text;
    }

    @Override
    public User getAuthor() {
        return author;
    }

    protected void setAuthor(User author) {
        this.author = author;
    }
}
