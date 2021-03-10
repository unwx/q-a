package qa.dao;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.NestedEntity;
import qa.dao.databasecomponents.Table;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class HqlBuilderTest {

    HqlBuilder<Entity> entityHqlBuilder = new HqlBuilder<>();
    List<String> entityDefaultFieldNames = new LinkedList<>() {
        @Serial
        private static final long serialVersionUID = 428839399058539852L;

        {
            add("id");
            add("str");
            add("date");
        }
    };

    @Test
    public void readNoNested() {
        Table table = new Table(entityDefaultFieldNames, "Entity");
        String hql = entityHqlBuilder.read(new Field("id", 5), table, Collections.emptyList());
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
    void readNoNested1_NullWhereValue() {
        Table table = new Table(Collections.singletonList("bool"), "Entity");
        String hql = entityHqlBuilder.read(new Field("where", null), table, Collections.emptyList());
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
    void readOnlyNested() {
        Table table = new Table(Collections.emptyList(), "Entity");
        NestedEntity nestedEntity = new NestedEntity(entityDefaultFieldNames, "nested1");
        String hql = entityHqlBuilder.read(new Field("id", 0), table, Collections.singletonList(nestedEntity));
        String required =
                """
                select\s\
                b.id as bb,b.str as bc,b.date as bd\s\
                from Entity a\s\
                inner join a.nested1 as b\s\
                where a.id=:a\
                """;
        assertThat(hql, equalTo(required));
    }

    @Test
    void readOnlyNestedEntities() {
        Table table = new Table(Collections.emptyList(), "Entity");
        NestedEntity nestedEntity1 = new NestedEntity(entityDefaultFieldNames, "nested1");
        NestedEntity nestedEntity2 = new NestedEntity(entityDefaultFieldNames, "nested2");
        List<NestedEntity> entities = new ArrayList<>();
        entities.add(nestedEntity1);
        entities.add(nestedEntity2);

        String hql = entityHqlBuilder.read(new Field("id", 0), table, entities);
        String required =
                """
                select\s\
                b.id as bb,b.str as bc,b.date as bd,\
                c.id as cb,c.str as cc,c.date as cd\s\
                from Entity a\s\
                inner join a.nested1 as b inner join a.nested2 as c\s\
                where a.id=:a\
                """;
        assertThat(hql, equalTo(required));
    }

    @Test
    void readAll() {
        Table table = new Table(entityDefaultFieldNames, "Entity");
        NestedEntity nestedEntity1 = new NestedEntity(entityDefaultFieldNames, "nested1");
        NestedEntity nestedEntity2 = new NestedEntity(entityDefaultFieldNames, "nested2");
        List<NestedEntity> entities = new ArrayList<>();
        entities.add(nestedEntity1);
        entities.add(nestedEntity2);

        String hql = entityHqlBuilder.read(new Field("id", 0), table, entities);
        String required =
                """
                select\s\
                a.id as ab,a.str as ac,a.date as ad,\
                b.id as bb,b.str as bc,b.date as bd,\
                c.id as cb,c.str as cc,c.date as cd\s\
                from Entity a\s\
                inner join a.nested1 as b inner join a.nested2 as c\s\
                where a.id=:a\
                """;
        assertThat(hql, equalTo(required));
    }

    @Test
    void readBigEntity() {
        HqlBuilder<BigEntity> hqlBuilder = new HqlBuilder<>();
        List<String> fieldNames = new LinkedList<>();
        fieldNames.add("l1");
        fieldNames.add("l2");
        fieldNames.add("l3");
        fieldNames.add("l4");
        fieldNames.add("l5");
        fieldNames.add("l6");
        fieldNames.add("l7");
        fieldNames.add("l8");
        fieldNames.add("l9");
        fieldNames.add("s1");
        fieldNames.add("s2");
        fieldNames.add("s3");
        fieldNames.add("s4");
        fieldNames.add("s5");
        fieldNames.add("s6");
        fieldNames.add("s7");
        fieldNames.add("s8");
        fieldNames.add("s9");
        fieldNames.add("b1");
        fieldNames.add("b2");
        fieldNames.add("b3");
        fieldNames.add("b4");
        fieldNames.add("b5");
        fieldNames.add("b6");
        fieldNames.add("b7");
        fieldNames.add("b8");
        fieldNames.add("b9");

        Table table = new Table(fieldNames, "BigEntity");
        String hql = hqlBuilder.read(new Field("l1", 0), table, Collections.emptyList());
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

    @Test
    public void updateTest() {
        Entity entity = new Entity(null, "test", true, null);
        ImmutablePair<String, Field[]> pair = entityHqlBuilder.update(new Field("id", 5L), entity, "Entity");
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
    public void updateBigEntityTest() {
        BigEntity bigEntity = new BigEntity(
                1L, 2L, 3L, 4L, 4L, 4L, 4L, 8L, 9L,
                "1", "2", "3", "3", "3", "6", "7", "8", "9",
                true, null, true, false, false, true, true, true, true
        );
        HqlBuilder<BigEntity> hqlBuilder = new HqlBuilder<>();
        ImmutablePair<String, Field[]> pair = hqlBuilder.update(new Field("id", 5L), bigEntity, "BigEntity");
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
