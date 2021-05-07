package qa.dao;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import qa.dao.database.components.Field;
import qa.dao.database.components.Table;
import qa.dao.database.components.Where;
import qa.dao.database.components.WhereOperator;
import qa.dao.entities.BigEntity;
import qa.dao.entities.Entity;
import qa.dao.entities.NestedEntity;
import qa.logger.TestLogger;
import qa.tools.annotations.TestClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@TestClass
public class HqlBuilderTest {

    private final HqlBuilder entityHqlBuilder = new HqlBuilder();
    private final String[] entityDefaultFieldNames = new String[] {
            "id",
            "str",
            "date"
    };

    private static final String LOG_NO_NESTED                       = "no nested entities";
    private static final String LOG_NO_NESTED_WHERE_VALUE_NULL      = "no nested entities. where value equals null";
    private static final String LOG_ALL_ENTITIES                    = "all entities";
    private static final String LOG_SIMPLE_ENTITY                   = "simple entity";
    private static final String LOG_BIG_ENTITY                      = "big entity";

    private static final String ID              = "id";
    private static final String ENTITY          = "Entity";
    private static final String BIG_ENTITY      = "BigEntity";

    private final TestLogger logger = new TestLogger(HqlBuilderTest.class);

    @Nested
    class read {

        @Test
        public void no_nested() {
            logger.trace(LOG_NO_NESTED);
            final Where where = new Where(ID, 5, WhereOperator.EQUALS);
            final Table table = new Table(entityDefaultFieldNames, ENTITY);

            final String hql = entityHqlBuilder.read(where, table, Collections.emptyList());
            final String required =
                    """
                    select\s\
                    a.id as ab,a.str as ac,a.date as ad\s\
                    from Entity a\s\
                    where a.id=:a\
                    """;

            assertThat(hql, equalTo(required));
        }

        @Test
        void no_nested_null_where_value() {
            logger.trace(LOG_NO_NESTED_WHERE_VALUE_NULL);
            final Where where = new Where(ID, null, WhereOperator.EQUALS);
            final Table table = new Table(new String[]{"bool"}, ENTITY);

            final String hql = entityHqlBuilder.read(where, table, Collections.emptyList());
            final String required =
                    """
                    select\s\
                    a.bool as ab\s\
                    from Entity a\s\
                    where a.id=:a\
                    """;

            assertThat(hql, equalTo(required));
        }

        @Nested
        class nested_only {

            @Test
            void first() {
                logger.trace("(1)");
                final Table table = new Table(new String[]{}, "Entity");
                final Where where = new Where(ID, 1L, WhereOperator.EQUALS);
                final qa.dao.database.components.NestedEntity nestedEntity = new qa.dao.database.components.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);

                final String hql = entityHqlBuilder.read(where, table, Collections.singletonList(nestedEntity));
                final String required =
                        """
                        select\s\
                        b.id as bb,b.str as bc,b.date as bd\s\
                        from Entity a\s\
                        inner join a.nestedEntity as b\s\
                        where a.id=:a\
                        """;

                assertThat(hql, equalTo(required));
            }

            @Test
            void second() {
                logger.trace("(2)");
                final Where where = new Where(ID, 0, WhereOperator.EQUALS);
                final Table table = new Table(new String[]{}, "Entity");
                final qa.dao.database.components.NestedEntity nestedEntity1 = new qa.dao.database.components.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);
                final qa.dao.database.components.NestedEntity nestedEntity2 = new qa.dao.database.components.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);


                final List<qa.dao.database.components.NestedEntity> entities = new ArrayList<>();
                entities.add(nestedEntity1);
                entities.add(nestedEntity2);

                final String hql = entityHqlBuilder.read(where, table, entities);
                final String required =
                        """
                        select\s\
                        b.id as bb,b.str as bc,b.date as bd,\
                        c.id as cb,c.str as cc,c.date as cd\s\
                        from Entity a\s\
                        inner join a.nestedEntity as b inner join a.nestedEntity as c\s\
                        where a.id=:a\
                        """;

                assertThat(hql, equalTo(required));
            }
        }

        @Test
        void all() {
            logger.trace(LOG_ALL_ENTITIES);
            final Where where = new Where(ID, 0, WhereOperator.EQUALS);
            final Table table = new Table(entityDefaultFieldNames, "Entity");
            final qa.dao.database.components.NestedEntity nestedEntity1 = new qa.dao.database.components.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);
            final qa.dao.database.components.NestedEntity nestedEntity2 = new qa.dao.database.components.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);

            final List<qa.dao.database.components.NestedEntity> entities = new ArrayList<>();
            entities.add(nestedEntity1);
            entities.add(nestedEntity2);

            final String hql = entityHqlBuilder.read(where, table, entities);
            final String required =
                    """
                    select\s\
                    a.id as ab,a.str as ac,a.date as ad,\
                    b.id as bb,b.str as bc,b.date as bd,\
                    c.id as cb,c.str as cc,c.date as cd\s\
                    from Entity a\s\
                    inner join a.nestedEntity as b inner join a.nestedEntity as c\s\
                    where a.id=:a\
                    """;

            assertThat(hql, equalTo(required));
        }

        @Test
        void big_entity() {
            logger.trace("big entity");
            final HqlBuilder hqlBuilder = new HqlBuilder();
            final String[] fieldNames = new String[]{
                    "l1",
                    "l2",
                    "l3",
                    "l4",
                    "l5",
                    "l6",
                    "l7",
                    "l8",
                    "l9",
                    "s1",
                    "s2",
                    "s3",
                    "s4",
                    "s5",
                    "s6",
                    "s7",
                    "s8",
                    "s9",
                    "b1",
                    "b2",
                    "b3",
                    "b4",
                    "b5",
                    "b6",
                    "b7",
                    "b8",
                    "b9"
            };

            final Where where = new Where("l1", 0, WhereOperator.EQUALS);
            final Table table = new Table(fieldNames, BIG_ENTITY);
            final String hql = hqlBuilder.read(where, table, Collections.emptyList());
            final String required =
                    """
                    select\s\
                    a.l1 as ab,a.l2 as ac,a.l3 as ad,a.l4 as ae,a.l5 as af,a.l6 as ag,a.l7 as ah,a.l8 as ai,a.l9 as aj,\
                    a.s1 as ak,a.s2 as al,a.s3 as am,a.s4 as an,a.s5 as ao,a.s6 as ap,a.s7 as aq,a.s8 as ar,a.s9 as as,\
                    a.b1 as at,a.b2 as au,a.b3 as av,a.b4 as aw,a.b5 as ax,a.b6 as ay,a.b7 as az,a.b8 as ab_,a.b9 as ac_\s\
                    from BigEntity a\s\
                    where a.l1=:a\
                    """;

            assertThat(hql, equalTo(required));
        }
    }

    @Nested
    class update {

        @Test
        public void simple_entity() {
            logger.trace(LOG_SIMPLE_ENTITY);
            final Entity entity = new Entity(null, "test", true, null);
            final Where where = new Where("id", 5L, WhereOperator.EQUALS);

            final ImmutablePair<String, Field[]> pair = entityHqlBuilder.update(where, entity);
            final String required =
                    """
                    update Entity a\s\
                    set\s\
                    a.str=:b,a.bool=:c\s\
                    where a.id=:a\
                    """;
            final Field[] requiredF = new Field[] {
                    new Field("b", "test"),
                    new Field("c", true)
            };

            assertThat(pair.left, equalTo(required));
            for (int i = 0; i < requiredF.length; i++) {
                assertThat(requiredF[i].getName(), equalTo(pair.right[i].getName()));
                assertThat(requiredF[i].getValue(), equalTo(pair.right[i].getValue()));
            }
        }

        @Test
        public void big_entity() {
            logger.trace(LOG_BIG_ENTITY);
            final BigEntity bigEntity = new BigEntity(
                    1L, 2L, 3L, 4L, 4L, 4L, 4L, 8L, 9L,
                    "1", "2", "3", "3", "3", "6", "7", "8", "9",
                    true, null, true, false, false, true, true, true, true
            );
            final HqlBuilder hqlBuilder = new HqlBuilder();
            final ImmutablePair<String, Field[]> pair = hqlBuilder.update(new Where("id", 5L, WhereOperator.EQUALS), bigEntity);
            final String required =
                    """
                    update BigEntity a\s\
                    set\s\
                    a.l1=:b,a.l2=:c,a.l3=:d,a.l4=:e,a.l5=:f,a.l6=:g,a.l7=:h,a.l8=:i,a.l9=:j,\
                    a.s1=:k,a.s2=:l,a.s3=:m,a.s4=:n,a.s5=:o,a.s6=:p,a.s7=:q,a.s8=:r,a.s9=:s,\
                    a.b1=:t,a.b3=:u,a.b4=:v,a.b5=:w,a.b6=:x,a.b7=:y,a.b8=:z,a.b9=:bb\s\
                    where a.id=:a\
                    """;
            final Field[] requiredF = new Field[]{
                    new Field("b", 1L),
                    new Field("c", 2L),
                    new Field("d", 3L),
                    new Field("e", 4L),
                    new Field("f", 4L),
                    new Field("g", 4L),
                    new Field("h", 4L),
                    new Field("i", 8L),
                    new Field("j", 9L),
                    new Field("k", "1"),
                    new Field("l", "2"),
                    new Field("m", "3"),
                    new Field("n", "3"),
                    new Field("o", "3"),
                    new Field("p", "6"),
                    new Field("q", "7"),
                    new Field("r", "8"),
                    new Field("s", "9"),
                    new Field("t", true),
                    new Field("u", true),
                    new Field("v", false),
                    new Field("w", false),
                    new Field("x", true),
                    new Field("y", true),
                    new Field("z", true),
                    new Field("bb", true),
            };

            assertThat(pair.left, equalTo(required));
            for (int i = 0; i < requiredF.length; i++) {
                assertThat(requiredF[i].getValue(), equalTo(pair.right[i].getValue()));
            }
        }
    }

    @Nested
    class delete {

        @Test
        public void entity() {
            logger.trace(LOG_SIMPLE_ENTITY);
            final Where where = new Where(ID, 5L, WhereOperator.EQUALS);
            final String required =
                    """
                    delete from Entity a\s\
                    where a.id=:a\
                    """;
            final String result = entityHqlBuilder.delete(ENTITY, where);
            assertThat(required, equalTo(result));
        }
    }

    @Nested
    class exist {

        @Test
        public void entity() {
            logger.trace(LOG_SIMPLE_ENTITY);
            final Where where = new Where(ID, 1L, WhereOperator.EQUALS);
            final String required =
                    """
                    select a.id from Entity a\s\
                    where a.id=:a\
                    """;
            final String result = entityHqlBuilder.exist(ENTITY, where);
            assertThat(required, equalTo(result));
        }
    }
}
