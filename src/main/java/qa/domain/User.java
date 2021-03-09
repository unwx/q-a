package qa.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;

import javax.persistence.*;

@Entity
@Table(name = "usr")
public class User implements FieldExtractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 30, nullable = false, unique = true)
    private String username;

    @Column(name = "about", length = 1024)
    private String about;


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id")
    private AuthenticationData authentication;


    public User(String username,
                String about) {
        this.username = username;
        this.about = about;
    }

    public User(Long id,
                String username,
                String about) {
        this.id = id;
        this.username = username;
        this.about = about;
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


    @Override
    public Field[] extract() {
        return new Field[]{
                new Field("id", id),
                new Field("username", username),
                new Field("about", about)};
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

        public User build() {
            return this.user;
        }
    }
}
