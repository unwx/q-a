package qa.domain;

import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldDataSetterExtractor;
import qa.dao.databasecomponents.FieldExtractor;
import qa.domain.setters.SetterField;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "usr")
public class User implements FieldExtractor, FieldDataSetterExtractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 30, nullable = false, unique = true)
    private String username;

    @Column(name = "about", length = 1024)
    private String about;

    @OneToMany(mappedBy = "author", cascade = {
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH})
    private List<Question> questions;

    @OneToMany(mappedBy = "author", cascade = {
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH})
    private List<Answer> answers;

    public User(Long id,
                String username,
                String about,
                List<Question> questions,
                List<Answer> answers) {
        this.id = id;
        this.username = username;
        this.about = about;
        this.questions = questions;
        this.answers = answers;
    }

    public User(String username,
                String about,
                List<Question> questions,
                List<Answer> answers) {
        this.username = username;
        this.about = about;
        this.questions = questions;
        this.answers = answers;
    }

    public User(Long id) {
        this.id = id;
    }

    public User() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    @Override
    public SetterField[] extractSettersField() {
        return new SetterField[]{
                new SetterField("id", Long.class),
                new SetterField("username", String.class),
                new SetterField("about", String.class),
        };
    }

    @Override
    public Field[] extract() {
        return new Field[]{
                new Field("id", id),
                new Field("username", username),
                new Field("about", about),
        };
    }

    public static class Builder {

        private final User user;

        public Builder() {
            this.user = new User();
        }

        public Builder id(Long id) {
            user.id = id;
            return this;
        }

        public Builder username(String username) {
            user.username = username;
            return this;
        }

        public Builder about(String about) {
            user.about = about;
            return this;
        }

        public Builder questions(List<Question> questions) {
            user.questions = questions;
            return this;
        }

        public Builder answers(List<Answer> answers) {
            user.answers = answers;
            return this;
        }

        public User build() {
            return this.user;
        }
    }
}
