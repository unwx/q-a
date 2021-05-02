package qa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import qa.dao.database.components.Field;
import qa.dao.database.components.FieldDataSetterExtractor;
import qa.dao.database.components.FieldExtractor;
import qa.domain.setters.SetterField;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "authentication")
public class AuthenticationData implements FieldExtractor, FieldDataSetterExtractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "email", length = 64, nullable = false, unique = true)
    private String email; //login

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "access_token_exp_date", length = 10, nullable = false)
    @JsonIgnore
    private Long accessTokenExpirationDateAtMills;

    @Column(name = "refresh_token_exp_date", length = 10, nullable = false)
    @JsonIgnore
    private Long refreshTokenExpirationDateAtMillis;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "auth_id"))
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private List<UserRole> roles;


    public AuthenticationData(User user,
                              String password,
                              String email,
                              Boolean enabled,
                              Long accessTokenExpirationDateAtMills,
                              Long refreshTokenExpirationDateAtMillis,
                              List<UserRole> roles) {
        this.user = user;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.accessTokenExpirationDateAtMills = accessTokenExpirationDateAtMills;
        this.refreshTokenExpirationDateAtMillis = refreshTokenExpirationDateAtMillis;
        this.roles = roles;
    }

    public AuthenticationData(Long id,
                              User user,
                              String password,
                              String email,
                              Boolean enabled,
                              Long accessTokenExpirationDateAtMills,
                              Long refreshTokenExpirationDateAtMillis,
                              List<UserRole> roles) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.accessTokenExpirationDateAtMills = accessTokenExpirationDateAtMills;
        this.refreshTokenExpirationDateAtMillis = refreshTokenExpirationDateAtMillis;
        this.roles = roles;
    }

    public AuthenticationData() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getAccessTokenExpirationDateAtMills() {
        return accessTokenExpirationDateAtMills;
    }

    public void setAccessTokenExpirationDateAtMills(Long accessTokenExpirationDateAtMills) {
        this.accessTokenExpirationDateAtMills = accessTokenExpirationDateAtMills;
    }

    public Long getRefreshTokenExpirationDateAtMillis() {
        return refreshTokenExpirationDateAtMillis;
    }

    public void setRefreshTokenExpirationDateAtMillis(Long refreshTokenExpirationDateAtMillis) {
        this.refreshTokenExpirationDateAtMillis = refreshTokenExpirationDateAtMillis;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    @Override
    public Field[] extract() {
        return new Field[]{
                new Field("id", id),
                new Field("password", password),
                new Field("email", email),
                new Field("enabled", enabled),
                new Field("accessTokenExpirationDateAtMills", accessTokenExpirationDateAtMills),
                new Field("refreshTokenExpirationDateAtMillis", refreshTokenExpirationDateAtMillis)
        };
    }

    @Override
    public SetterField[] extractSettersField() {
        return new SetterField[]{
                new SetterField("id", Long.class),
                new SetterField("password", String.class),
                new SetterField("email", String.class),
                new SetterField("enabled", Boolean.class),
                new SetterField("accessTokenExpirationDateAtMills", Long.class),
                new SetterField("refreshTokenExpirationDateAtMillis", Long.class),
                new SetterField("user", User.class),
        };
    }

    @Override
    public String getClassName() {
        return "AuthenticationData";
    }

    public static class Builder {
        private final AuthenticationData data;

        public Builder() {
            this.data = new AuthenticationData();
        }

        public Builder id(Long id) {
            data.id = id;
            return this;
        }

        public Builder password(String password) {
            data.password = password;
            return this;
        }

        public Builder email(String email) {
            data.email = email;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            data.enabled = enabled;
            return this;
        }

        public Builder accessTokenExpirationDateAtMillis(Long access) {
            data.accessTokenExpirationDateAtMills = access;
            return this;
        }

        public Builder refreshTokenExpirationDateAtMillis(Long refresh) {
            data.refreshTokenExpirationDateAtMillis = refresh;
            return this;
        }

        public Builder user(User user) {
            data.user = user;
            return this;
        }

        public Builder roles(List<UserRole> roles) {
            data.roles = roles;
            return this;
        }

        public AuthenticationData build() {
            return data;
        }
    }
}
