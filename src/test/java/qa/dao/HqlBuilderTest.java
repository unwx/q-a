package qa.dao;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import qa.TestLogger;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.Table;
import qa.dao.databasecomponents.Where;
import qa.dao.databasecomponents.WhereOperator;
import qa.entities.BigEntity;
import qa.entities.Entity;
import qa.entities.NestedEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class HqlBuilderTest {

    HqlBuilder entityHqlBuilder = new HqlBuilder();
    String[] entityDefaultFieldNames = new String[] {
            "id",
            "str",
            "date"
    };
    private static final Logger logger = LogManager.getLogger(HqlBuilderTest.class);

    @Nested
    class read {
        @Test
        public void no_nested() {
            TestLogger.trace(logger, "read -> no nested", 3);
            Table table = new Table(entityDefaultFieldNames, "Entity");
            String hql = entityHqlBuilder.read(new Where("id", 5, WhereOperator.EQUALS), table, Collections.emptyList());
            String required =
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
            TestLogger.trace(logger, "read -> no nested null where value", 3);
            Table table = new Table(new String[]{"bool"}, "Entity");
            String hql = entityHqlBuilder.read(new Where("where", null, WhereOperator.EQUALS), table, Collections.emptyList());
            String required =
                    """
                    select\s\
                    a.bool as ab\s\
                    from Entity a\s\
                    where a.where=:a\
                    """;
            assertThat(hql, equalTo(required));
        }

        @Test
        void nested_only_1() {
            TestLogger.trace(logger, "read -> nested only", 3);
            Table table = new Table(new String[]{}, "Entity");

            qa.dao.databasecomponents.NestedEntity nestedEntity = new qa.dao.databasecomponents.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);
            String hql = entityHqlBuilder.read(new Where("id", 1L, WhereOperator.EQUALS), table, Collections.singletonList(nestedEntity));
            String required =
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
        void nested_only_2() {
            TestLogger.trace(logger, "read -> nested only", 3);
            Table table = new Table(new String[]{}, "Entity");
            qa.dao.databasecomponents.NestedEntity nestedEntity1 = new qa.dao.databasecomponents.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);
            qa.dao.databasecomponents.NestedEntity nestedEntity2 = new qa.dao.databasecomponents.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);
            List<qa.dao.databasecomponents.NestedEntity> entities = new ArrayList<>();
            entities.add(nestedEntity1);
            entities.add(nestedEntity2);

            String hql = entityHqlBuilder.read(new Where("id", 0, WhereOperator.EQUALS), table, entities);
            String required =
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

        @Test
        void all() {
            TestLogger.trace(logger, "read -> all", 3);
            Table table = new Table(entityDefaultFieldNames, "Entity");
            qa.dao.databasecomponents.NestedEntity nestedEntity1 = new qa.dao.databasecomponents.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);
            qa.dao.databasecomponents.NestedEntity nestedEntity2 = new qa.dao.databasecomponents.NestedEntity(entityDefaultFieldNames, NestedEntity.class, "nestedEntity", null);
            List<qa.dao.databasecomponents.NestedEntity> entities = new ArrayList<>();
            entities.add(nestedEntity1);
            entities.add(nestedEntity2);

            String hql = entityHqlBuilder.read(new Where("id", 0, WhereOperator.EQUALS), table, entities);
            String required =
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
            TestLogger.trace(logger, "read -> big entity", 3);
            HqlBuilder hqlBuilder = new HqlBuilder();
            String[] fieldNames = new String[]{
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

            Table table = new Table(fieldNames, "BigEntity");
            String hql = hqlBuilder.read(new Where("l1", 0, WhereOperator.EQUALS), table, Collections.emptyList());
            String required =
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
            TestLogger.trace(logger, "update -> simple entity", 3);
            Entity entity = new Entity(null, "test", true, null);
            ImmutablePair<String, Field[]> pair = entityHqlBuilder.update(new Where("id", 5L, WhereOperator.EQUALS), entity);
            String required =
                    """
                    update Entity a\s\
                    set\s\
                    a.str=:b,a.bool=:c\s\
                    where a.id=:a\
                    """;
            Field[] requiredF = new Field[] {
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
            TestLogger.trace(logger, "update -> big entity", 3);
            BigEntity bigEntity = new BigEntity(
                    1L, 2L, 3L, 4L, 4L, 4L, 4L, 8L, 9L,
                    "1", "2", "3", "3", "3", "6", "7", "8", "9",
                    true, null, true, false, false, true, true, true, true
            );
            HqlBuilder hqlBuilder = new HqlBuilder();
            ImmutablePair<String, Field[]> pair = hqlBuilder.update(new Where("id", 5L, WhereOperator.EQUALS), bigEntity);
            String required =
                    """
                    update BigEntity a\s\
                    set\s\
                    a.l1=:b,a.l2=:c,a.l3=:d,a.l4=:e,a.l5=:f,a.l6=:g,a.l7=:h,a.l8=:i,a.l9=:j,\
                    a.s1=:k,a.s2=:l,a.s3=:m,a.s4=:n,a.s5=:o,a.s6=:p,a.s7=:q,a.s8=:r,a.s9=:s,\
                    a.b1=:t,a.b3=:u,a.b4=:v,a.b5=:w,a.b6=:x,a.b7=:y,a.b8=:z,a.b9=:bb\s\
                    where a.id=:a\
                    """;
            Field[] requiredF = new Field[]{
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
            TestLogger.trace(logger, "delete -> entity", 3);
            String className = "Entity";
            Where where = new Where("id", 5L, WhereOperator.EQUALS);
            String required =
                    """
                    delete from Entity a\s\
                    where a.id=:a\
                    """;
            String result = entityHqlBuilder.delete(className, where);
            assertThat(required, equalTo(result));
        }
    }

    @Nested
    class exist {
        @Test
        public void entity() {
            TestLogger.trace(logger, "exist -> entity", 3);
            String className = "Entity";
            Where where = new Where("id", 1L, WhereOperator.EQUALS);
            String required =
                    """
                    select a.id from Entity a\s\
                    where a.id=:a\
                    """;
            String result = entityHqlBuilder.exist(className, where);
            assertThat(required, equalTo(result));
        }
    }
}
