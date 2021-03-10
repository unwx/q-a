package qa.dao;

import org.apache.commons.lang3.tuple.ImmutablePair;
import qa.dao.databasecomponents.*;

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
    public final char DEFAULT_WHERE_PARAM_NAME = 'a';

    /**
     * @param nested @Nullable.
     * @return hqlQuery as string.
     * <h3>param = 'a'.</h3>
     */
    public String read(Field where, Table target, List<NestedEntity> nested) {
        StringBuilder hqlBuilder = new StringBuilder();
        prepareForRead(hqlBuilder);
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
    public ImmutablePair<String, Field[]> update(Field where, Entity entity, String className) {
        StringBuilder hqlBuilder = new StringBuilder();
        prepareForUpdate(className, hqlBuilder);
        Field[] fields = setParameterMarks(entity, hqlBuilder);
        where(where, hqlBuilder);
        return new ImmutablePair<>(hqlBuilder.toString(), fields);
    }

    private void selectProcess(Table target, List<NestedEntity> nested, StringBuilder hqlBuilder) {
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
            while (times > 0) {
                for (int i = times; i > times - 25; i--) {
                    set(fields[i].getName(), hqlBuilder, abb + abbreviated[i]);
                    /*
                     * replace the field names inserted in the query into their labeled names,
                     *  so that later they can be inserted as parameters ("param_name": "param_value")
                     */
                    fields[i].setName(abb + abbreviated[i]);
                }
                times -= 25;
                abbIndex++;
                abb = String.valueOf(abbreviated[abbIndex]);
            }
        }
        removeTrash(hqlBuilder);
        hqlBuilder.append(' ');
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

    private void joinProcess(List<NestedEntity> nested, StringBuilder hqlBuilder) {
        for (int i = 0; i < nested.size(); i++) {
            join(nested.get(i), abbreviated[i], hqlBuilder);
        }
    }

    private void whereProcess(Field field, StringBuilder hqlBuilder) {
        where(field, hqlBuilder);
    }

    private void prepareForRead(StringBuilder hqlBuilder) {
        hqlBuilder.append("select ");
    }

    private void prepareForUpdate(String className, StringBuilder hqlBuilder) {
        hqlBuilder
                .append("update ")
                .append(className)
                .append(" as ")
                .append(tl)
                .append(" set ");
    }

    private void select(EntityTable t, char abb, StringBuilder hqlBuilder) {
        t.getFieldNames().forEach((f) ->
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
                .append(t.getClassName())
                .append(" as ")
                .append(tl)
                .append(' ');
    }

    private void join(NestedEntity table, char abb, StringBuilder hqlBuilder) {
        hqlBuilder
                .append("inner join ")
                .append(tl)
                .append('.')
                .append(table.getClassName())
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
                .append(':')
                .append(DEFAULT_WHERE_PARAM_NAME);
    }

    private void removeTrash(StringBuilder hqlBuilder) {
        hqlBuilder.deleteCharAt(hqlBuilder.length() - 1);
    }
}