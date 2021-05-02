package qa.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import qa.cache.like.HasLiked;
import qa.cache.like.HasLikes;
import qa.dao.database.components.FieldDataSetterExtractor;
import qa.dao.database.components.FieldExtractor;
import qa.service.util.HasAuthor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "comment_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Comment implements FieldExtractor, FieldDataSetterExtractor, HasAuthor, HasLikes, HasLiked {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "text", length = 500, nullable = false)
    protected String text;

    @Column(name = "creation_date", nullable = false)
    protected Date creationDate;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class, cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    protected User author;

    @Transient
    protected int likes;

    @Transient
    protected boolean liked;


    protected Comment(Long id,
                      String text,
                      Date creationDate,
                      User author) {
        this.id = id;
        this.text = text;
        this.creationDate = creationDate;
        this.author = author;
    }

    protected Comment(String text,
                      Date creationDate,
                      User author) {
        this.text = text;
        this.creationDate = creationDate;
        this.author = author;
    }

    protected Comment() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("creation_date")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    public boolean isLiked() {
        return this.liked;
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
    public String getIdStr() {
        return String.valueOf(id);
    }

    @Override
    public String getClassName() {
        return "Comment";
    }
}
