package qa.dao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;
import qa.domain.AuthenticationData;

import java.util.List;

@Component
public class AuthenticationDao implements Dao<AuthenticationData, Long> {

    private final DaoImpl<AuthenticationData> dao = new DaoImpl<>(AuthenticationData.class);

    @Override
    public Long create(@NotNull final AuthenticationData data) {
        return (Long) dao.create(data);
    }

    @Override
    @Nullable
    public AuthenticationData read(@NotNull final Field where, @NotNull final Table target) {
        return dao.read(where, target);
    }

    @Override
    @Nullable
    public AuthenticationData read(@NotNull final Field where, @NotNull final Table target, @NotNull final List<NestedEntity> nested) {
        return dao.read(where, target, nested);
    }

    @Override
    public List<AuthenticationData> readMany(@NotNull final Field where, @NotNull final Table target) {
        return dao.readMany(where, target);
    }

    @Override
    public List<AuthenticationData> readMany(@NotNull final Field where, @NotNull final Table target, @NotNull final List<NestedEntity> nested) {
        return dao.readMany(where, target, nested);
    }

    @Override
    public void update(@NotNull final Field where, @NotNull final AuthenticationData data, @NotNull final String className) {
        dao.update(where, data, className);
    }

    @Override
    public void updateEager(AuthenticationData data) {
        dao.updateEager(data);
    }

    @Override
    public void delete(@NotNull final AuthenticationData data) {
        dao.delete(data);
    }
}
