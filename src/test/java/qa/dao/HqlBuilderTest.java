package qa.dao;

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
        String hql = entityHqlBuilder.read(new Field("l1", 0), table, Collections.emptyList());
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
