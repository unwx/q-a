package qa.dao.query.manager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.query.convertor.AuthenticationQueryResultConvertor;
import qa.dao.query.creator.AuthenticationQueryCreator;
import qa.domain.AuthenticationData;
import qa.dto.internal.hibernate.entities.authentication.AuthenticationWithTokensDto;

public class AuthenticationQueryManager {

    private AuthenticationQueryManager() {}

    public static Query<?> getPasswordQuery(String email, Session session) {
        return AuthenticationQueryCreator.getPasswordQuery(email, session);
    }

    public static Query<AuthenticationWithTokensDto> getAuthQuery(String email, Session session) {
        return AuthenticationQueryCreator.getAuthWithTokensQuery(email, session);
    }

    public static AuthenticationData dtoToAuthData(AuthenticationWithTokensDto dto) {
        return AuthenticationQueryResultConvertor.dtoToAuthData(dto);
    }
}