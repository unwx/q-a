package qa.dao.daoutil;

import org.apache.commons.lang3.tuple.ImmutablePair;
import qa.dao.databasecomponents.Field;
import qa.dao.databasecomponents.FieldExtractor;
import qa.dao.databasecomponents.Table;

import java.util.Arrays;
import java.util.List;

/**
 * RESERVED:
 * ':a' - where.
 */
public class HqlBuilder<Entity extends FieldExtractor> {
    private final char[] abbreviated = new char[]{
            'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z',
    };
    private final char tl = 'a'; //target letter

    /**
     * @param nested @Nullable.
     * @return hqlQuery as string.
     * <h3>param = 'a'.</h3>
     */
    public String read(Field where, Table target, List<Table> nested) {
        StringBuilder hqlBuilder = new StringBuilder();
        hqlBuilder.append("select ");
        selectProcess(target, nested, hqlBuilder);
        fromProcess(target, hqlBuilder);
        joinProcess(nested, hqlBuilder);
        whereProcess(where, hqlBuilder);
        return hqlBuilder.toString();
    }

    /**
     * @return String - hql query;
     * Field[] - :params; ("param.name":"param.value")
     */
    public ImmutablePair<String, Field[]> update(Field where, Entity entity, String clz) {
        StringBuilder hqlBuilder = new StringBuilder();
        prepareForUpdate(clz, hqlBuilder);
        Field[] fields = setParameterMarks(entity, hqlBuilder);
        hqlBuilder.append(' ');
        where(where, hqlBuilder);
        return new ImmutablePair<>(hqlBuilder.toString(), fields);
    }

    private void prepareForUpdate(String clz, StringBuilder hqlBuilder) {
        hqlBuilder
                .append("update ")
                .append(clz)
                .append(" as ")
                .append(tl)
                .append(" set ");
    }

    private void selectProcess(Table target, List<Table> nested, StringBuilder hqlBuilder) {
        select(target, tl, hqlBuilder);
        for (int i = 1; i < nested.size(); i++) {
            select(nested.get(i), abbreviated[i], hqlBuilder);
        }
        removeTrash(hqlBuilder);
    }

    private Field[] setParameterMarks(Entity entity, StringBuilder hqlBuilder) {
        Field[] fields = nullFilterFields(entity.extract());
        fewFieldsAlgorithm(fields, hqlBuilder);
        String abb = String.valueOf(abbreviated[0]);
        int abbIndex = 0;
        if (fields.length > 25) {
            int times = fields.length - 25;
            while(times > 0) {
                for (int i = times; i > times - 25; i--) {
                    set(fields[i].getName(), hqlBuilder, abb + abbreviated[i]);
                    fields[i].setName(abb + abbreviated[i]);
                }
                times -= 25;
                abbIndex++;
                abb = String.valueOf(abbreviated[abbIndex]);
            }
        }
        removeTrash(hqlBuilder);
        return fields;
    }


    private void fewFieldsAlgorithm(Field[] fields, StringBuilder hqlBuilder) {
        for (int i = 0; i < 25; i++) {
            set(fields[i].getName(), hqlBuilder, abbreviated[i]);
            fields[i].setName(String.valueOf(abbreviated[i]));
        }
    }

    private Field[] nullFilterFields(Field[] fields) {
        return Arrays.stream(fields).filter((f) -> f.getValue() != null).toArray(Field[]::new);
    }

    private void fromProcess(Table t, StringBuilder hqlBuilder) {
        from(t, hqlBuilder);
    }

    private void joinProcess(List<Table> nested, StringBuilder hqlBuilder) {
        for (int i = 0; i < nested.size(); i++) {
            join(nested.get(i), abbreviated[i], hqlBuilder);
        }
    }

    private void whereProcess(Field field, StringBuilder hqlBuilder) {
        where(field, hqlBuilder);
    }

    private void select(Table t, char abb, StringBuilder hqlBuilder) {
        t.getFields().forEach((f) ->
                hqlBuilder
                        .append(abb)
                        .append('.')
                        .append(f)
                        .append("as ")
                        .append(f)
                        .append(',')

        );
    }

    private void set(String name, StringBuilder hqlBuilder, char abb) {
        hqlBuilder
                .append(tl)
                .append(name)
                .append("=:")
                .append(abb)
                .append(',');
    }

    private void set(String name, StringBuilder hqlBuilder, String abb) {
        hqlBuilder
                .append(tl)
                .append(name)
                .append("=:")
                .append(abb)
                .append(',');
    }

    private void from(Table t, StringBuilder hqlBuilder) {
        hqlBuilder
                .append("from ")
                .append(t.getClz())
                .append(" as ")
                .append(tl)
                .append(' ');
    }

    private void join(Table table, char abb, StringBuilder hqlBuilder) {
        hqlBuilder
                .append("inner join ")
                .append(tl)
                .append('.')
                .append(table.getClz())
                .append(" as ")
                .append(abb)
                .append(' ');
    }

    private void where(Field where, StringBuilder hqlBuilder) {
        hqlBuilder.
                append("where ")
                .append(tl)
                .append('.')
                .append(where.getName())
                .append('=')
                .append(":a");
    }

    private void removeTrash(StringBuilder hqlBuilder) {
        hqlBuilder.deleteCharAt(hqlBuilder.length() - 1);
    }
}