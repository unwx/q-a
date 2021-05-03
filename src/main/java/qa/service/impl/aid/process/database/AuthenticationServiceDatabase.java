package qa.service.impl.aid.process.database;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.stereotype.Component;
import qa.config.PasswordEncryptorFactory;
import qa.dao.AuthenticationDao;
import qa.dao.database.components.Table;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.domain.AuthenticationData;
import qa.domain.User;
import qa.domain.UserRole;
import qa.dto.internal.JwtPairDataDto;
import qa.dto.request.authentication.RegistrationRequest;
import qa.exceptions.rest.BadRequestException;
import qa.exceptions.rest.UnauthorizedException;

import java.util.Collections;

@Component
public class AuthenticationServiceDatabase {

    private final PooledPBEStringEncryptor passwordEncryptor;
    private final AuthenticationDao authenticationDao;

    private static final String ID                      = "id";
    private static final String EMAIL                   = "email";
    private static final String AUTH_CLASS_NAME         = "AuthenticationData";

    private static final String USERNAME                = "username";
    private static final String USER_CLASS_NAME         = "User";

    private static final String ERR_WRONG_AUTH      = "wrong login or password";
    private static final String ERR_USER_ALREADY_EXIST  = "user already exist";
    private static final String ERR_USERNAME_EXIST      = "user with this username already exist";


    public AuthenticationServiceDatabase(PasswordEncryptorFactory passwordEncryptorFactory,
                                         AuthenticationDao authenticationDao) {
        this.passwordEncryptor = passwordEncryptorFactory.create();
        this.authenticationDao = authenticationDao;
    }

    public void saveNewUser(RegistrationRequest request, JwtPairDataDto dto) {
        final User user = new User.Builder()
                .username(request.getUsername())
                .build();

        final AuthenticationData data = new AuthenticationData.Builder()
                .email(request.getEmail())
                .password(passwordEncryptor.encrypt(request.getPassword()))
                .enabled(true)
                .accessTokenExpirationDateAtMillis(dto.getAccess().getExp())
                .refreshTokenExpirationDateAtMillis(dto.getRefresh().getExp())
                .user(user)
                .roles(Collections.singletonList(UserRole.USER))
                .build();

        this.authenticationDao.create(data);
    }

    /**
     *
     * @throws UnauthorizedException:
     * wrong password
     */
    public void authenticate(AuthenticationData data) {
        final boolean isPasswordCorrect = this.authenticationDao.isEmailPasswordCorrect(
                data.getEmail(),
                data.getPassword(),
                this.passwordEncryptor
        );
        if (!isPasswordCorrect) {
            throw new UnauthorizedException(ERR_WRONG_AUTH);
        }
    }

    public void refreshUserTokensExpirationTime(String email, JwtPairDataDto jwtPair) {
        final AuthenticationData data = new AuthenticationData.Builder()
                .accessTokenExpirationDateAtMillis(jwtPair.getAccess().getExp())
                .refreshTokenExpirationDateAtMillis(jwtPair.getRefresh().getExp())
                .build();
        final Where where = new Where(EMAIL, email, WhereOperator.EQUALS);

        this.authenticationDao.update(where, data);
    }

    public boolean isUserAlreadyExist(RegistrationRequest request) {
        final Where where = new Where(EMAIL, request.getEmail(), WhereOperator.EQUALS);
        final Table table = new Table(new String[] {ID}, AUTH_CLASS_NAME);

        final AuthenticationData data = this.authenticationDao.read(where, table);
        return data != null;
    }

    public boolean isUsernameAlreadyExist(RegistrationRequest request) {
        final Where where = new Where(USERNAME, request.getUsername(), WhereOperator.EQUALS);
        final Table table = new Table(new String[] {ID}, USER_CLASS_NAME);

        final AuthenticationData data = this.authenticationDao.read(where, table);
        return data != null;
    }

    /**
     *
     * @throws BadRequestException:
     * if user exist;
     * if username exist
     */
    public void checkExistence(RegistrationRequest request) {
        if (this.isUserAlreadyExist(request)) throw new BadRequestException(ERR_USER_ALREADY_EXIST);
        if (this.isUsernameAlreadyExist(request)) throw new BadRequestException(ERR_USERNAME_EXIST);
    }
}
