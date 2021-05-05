package qa.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.dao.query.manager.AuthenticationQueryManager;
import qa.domain.AuthenticationData;
import qa.domain.setters.PropertySetterFactory;
import qa.dto.internal.hibernate.entities.authentication.AuthenticationWithTokensDto;

import java.util.List;

@Component
public class AuthenticationDao extends DaoImpl<AuthenticationData> {

    private final SessionFactory sessionFactory;

    @Autowired
    public AuthenticationDao(PropertySetterFactory propertySetterFactory, SessionFactory sessionFactory) {
        super(sessionFactory, propertySetterFactory.getSetter(new AuthenticationData()));
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Long create(AuthenticationData e) {
        return (Long) super.create(e);
    }

    public boolean isEmailPasswordCorrect(String email, String password, PooledPBEStringEncryptor passwordEncryptor) {
        try(Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            final String realPassword = (String) AuthenticationQueryManager
                    .getPasswordQuery(email, session)
                    .uniqueResult();

            transaction.commit();
            if (realPassword == null)
                return false;

            return passwordEncryptor.decrypt(realPassword).equals(password);
        }
    }

    @Nullable
    public AuthenticationData getAuthWithTokens(String email) {
        final AuthenticationData data;
        final AuthenticationWithTokensDto dto;

        try(Session session = sessionFactory.openSession()) {
            final Transaction transaction = session.beginTransaction();

            List<AuthenticationWithTokensDto> temp = AuthenticationQueryManager.getAuthQuery(email, session).list();
            dto = AuthenticationQueryManager
                    .getAuthQuery(email, session)
                    .uniqueResult();

            if (dto == null) {
                transaction.rollback();
                return null;
            }
        }

        data = AuthenticationQueryManager.dtoToAuthData(dto);
        return data;
    }
}
