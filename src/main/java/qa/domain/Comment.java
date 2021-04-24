package qa.domain;

import qa.cache.Cached;
import qa.cache.entity.like.HasLiked;
import qa.cache.entity.like.HasLikes;
import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.dao.databasecomponents.FieldExtractor;
import qa.util.access.HasAuthor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "comment_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Comment implements FieldExtractor, FieldDataSetterExtractor, HasAuthor, HasLikes<Long>, HasLiked {

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

    @Cached
    @Transient
    protected int likes;

    @Cached
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

    protected Comment() {
    }


    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    protected Date getCreationDate() {
        return creationDate;
    }

    protected void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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
}
