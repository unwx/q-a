package qa.dto.internal.hibernate.entities.authentication;

import qa.domain.UserRole;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthenticationWithTokensDto {

    private final long id;
    private final long accessExp;
    private final long refreshExp;
    private final List<UserRole> roles;

    public static final String ID               = "aut_id";
    public static final String ACCESS_TOKEN     = "aut_access_t";
    public static final String REFRESH_TOKEN    = "aut_refresh_t";
    public static final String ROLES            = "aut_roles";

    public AuthenticationWithTokensDto(Object[] tuples,
                              Map<String, Integer> aliasToIndexMap) {
        this.id = ((BigInteger) tuples[aliasToIndexMap.get(ID)]).longValue();
        this.accessExp = ((BigInteger) tuples[aliasToIndexMap.get(ACCESS_TOKEN)]).longValue();
        this.refreshExp = ((BigInteger) tuples[aliasToIndexMap.get(REFRESH_TOKEN)]).longValue();
        this.roles = addRoles((String) tuples[aliasToIndexMap.get(ROLES)]);
    }

    public long getId() {
        return id;
    }

    public long getAccessExp() {
        return accessExp;
    }

    public long getRefreshExp() {
        return refreshExp;
    }

    private List<UserRole> addRoles(String roles) {
        final List<UserRole> userRoles = new ArrayList<>();
        for (String s : roles.split(",")) {
            switch (s) {
                case "USER" -> userRoles.add(UserRole.USER);
                case "MODERATOR" -> userRoles.add(UserRole.MODERATOR);
                case "ADMIN" -> userRoles.add(UserRole.ADMIN);
            }
        }
        return userRoles;
    }

    public List<UserRole> getRoles() {
        return roles;
    }
}
