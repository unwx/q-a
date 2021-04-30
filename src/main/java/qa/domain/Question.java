package qa.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import qa.cache.like.HasLiked;
import qa.cache.like.HasLikes;
import qa.dao.Domain;
import qa.dao.database.components.Field;
import qa.dao.database.components.FieldDataSetterExtractor;
import qa.dao.database.components.FieldExtractor;
import qa.domain.setters.SetterField;
import qa.util.access.HasAuthor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table
public class Question implements FieldExtractor, FieldDataSetterExtractor, HasAuthor, HasLikes, HasLiked, Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", length = 2000, nullable = false)
    private String text;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @Column(name = "last_activity", nullable = false)
    private Date lastActivity;

    @Column(name = "tags", nullable = false)
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class, cascade = {
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH
    })
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Answer> answers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CommentQuestion> comments;

    @Transient
    private int likes;

    @Transient
    private boolean liked;

    public Question(Long id,
                    String text,
                    String title,
                    Date creationDate,
                    Date lastActivity,
                    String tags,
                    User author,
                    List<Answer> answers,
                    List<CommentQuestion> comments) {
        this.id = id;
        this.text = text;
        this.title = title;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
        this.tags = tags;
        this.author = author;
        this.answers = answers;
        this.comments = comments;
    }

    public Question(String text,
                    String title,
                    Date creationDate,
                    Date lastActivity,
                    String tags,
                    User author,
                    List<Answer> answers,
                    List<CommentQuestion> comments) {
        this.text = text;
        this.title = title;
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
        this.tags = tags;
        this.author = author;
        this.answers = answers;
        this.comments = comments;
    }

    public Question(Long id) {
        this.id = id;
    }

    public Question() {
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) { // TODO implement update
        this.lastActivity = lastActivity;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<CommentQuestion> getComments() {
        return comments;
    }

    public void setComments(List<CommentQuestion> comments) {
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLikes() {
        return likes;
    }

    @Override
    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    public String getIdStr() {
        return String.valueOf(this.id);
    }

    @Override
    public SetterField[] extractSettersField() { // TODO optimize;
        return new SetterField[] {
                new SetterField("id", Long.class),
                new SetterField("text", String.class),
                new SetterField("title", String.class),
                new SetterField("creationDate", Date.class),
                new SetterField("lastActivity", Date.class),
                new SetterField("tags", String.class),
                new SetterField("author", User.class),
        };
    }

    @Override
    public Field[] extract() { // TODO refactor names
        return new Field[] {
                new Field("id", id),
                new Field("text", text),
                new Field("title", title),
                new Field("creationDate", creationDate),
                new Field("lastActivity", lastActivity),
                new Field("tags", tags),
                new Field("author", author),
        };
    }

    @Override
    public String getClassName() {
        return "Question";
    }

    public static class Builder {

        private final Question question;

        public Builder() {
            question = new Question();
        }

        public Builder id(Long id) {
            question.id = id;
            return this;
        }

        public Builder text(String text) {
            question.text = text;
            return this;
        }

        public Builder title(String title) {
            question.title = title;
            return this;
        }

        public Builder creationDate(Date creationDate) {
            question.creationDate = creationDate;
            return this;
        }

        public Builder lastActivity(Date lastActivity) {
            question.lastActivity = lastActivity;
            return this;
        }

        public Builder tags(String tags) {
            question.tags = tags;
            return this;
        }

        public Builder author(User author) {
            question.author = author;
            return this;
        }

        public Builder answers(List<Answer> answers) {
            question.answers = answers;
            return this;
        }

        public Builder comments(List<CommentQuestion> comments) {
            question.comments = comments;
            return this;
        }

        public Question build() {
            return question;
        }
    }
}