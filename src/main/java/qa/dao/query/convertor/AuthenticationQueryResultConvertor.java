package qa.dao.query.convertor;

import qa.domain.AuthenticationData;
import qa.dto.internal.hibernate.entities.authentication.AuthenticationWithTokensDto;

public class AuthenticationQueryResultConvertor {

    private AuthenticationQueryResultConvertor() {}

    public static AuthenticationData dtoToAuthData(AuthenticationWithTokensDto dto) {
        return new AuthenticationData.Builder()
                .id(dto.getId())
                .accessTokenExpirationDateAtMillis(dto.getAccessExp())
                .refreshTokenExpirationDateAtMillis(dto.getRefreshExp())
                .roles(dto.getRoles())
                .build();
    }
}
