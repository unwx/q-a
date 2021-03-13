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
    private final String[] abbreviated = new String[]{
            "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z",
    };
    private final String tl = "a"; //target letter
    public final String DEFAULT_WHERE_PARAM_NAME = "a";

    /**
     * @param nested @Nullable.
     * @return hqlQuery as string.
     * <h3>param = DEFAULT_WHERE_PARAM_NAME.</h3>
     */
    public String read(Where where, Table target, List<NestedEntity> nested) {
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
    public ImmutablePair<String, Field[]> update(Where where, Entity entity, String className) {
        StringBuilder hqlBuilder = new StringBuilder();
        prepareForUpdate(className, hqlBuilder);
        Field[] fields = setParameterMarks(entity, hqlBuilder);
        where(where, hqlBuilder);
        return new ImmutablePair<>(hqlBuilder.toString(), fields);
    }

    private void selectProcess(Table target, List<NestedEntity> nested, StringBuilder hqlBuilder) {
        String[] as = asGenerate("a", target.getFieldNames().length);
        select(target, tl, as, hqlBuilder);
        for (int i = 0; i < nested.size(); i++) {
            String[] _as = asGenerate(abbreviated[i], nested.get(i).getFieldNames().length);
            select(nested.get(i), abbreviated[i], _as, hqlBuilder);
        }
        removeTrash(hqlBuilder);
    }

    private Field[] setParameterMarks(Entity entity, StringBuilder hqlBuilder) {
        Field[] fields = nullFilterFields(entity.extract());
        fewFieldsAlgorithm(fields, hqlBuilder);
        if (fields.length > 25) {
            String abb = String.valueOf(abbreviated[0]);
            int abbIndex = 0;
            int times = fields.length - 25;
            while (times > 0) {
                for (int i = 0; i < Math.min(times, 25); i++) {
                    set(fields[i + 25].getName(), hqlBuilder, abb + abbreviated[i]);
                    /*
                     * replace the field names inserted in the query into their labeled names,
                     *  so that later they can be inserted as parameters ("param_name": "param_value")
                     */
                    fields[i + 25].setName(abb + abbreviated[i]);
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

    private String[] asGenerate(String symbol, int length) {
        String[] as = new String[length];
        int asIndex = 0;
        int times = length;
        while (times > 0) {
            for (int i = 0; i < Math.min(times, 25); i++) {
                as[asIndex] = symbol + abbreviated[i];
                asIndex++;
            }
            times -= 25;
        }
        if (length > 25) {
            int additionalSymbolTimes = 1;
            int _times = length - 25;
            while (_times > 0) {
                for (int i = 0; i < Math.min(_times, 25); i++) {
                    as[i + 25] += "_".repeat(additionalSymbolTimes);
                }
                _times -= 25;
                additionalSymbolTimes++;
            }

        }
        return as;
    }

    private void fewFieldsAlgorithm(Field[] fields, StringBuilder hqlBuilder) {
        for (int i = 0; i < Math.min(fields.length, 25); i++) {
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

    private void whereProcess(Where where, StringBuilder hqlBuilder) {
        where(where, hqlBuilder);
    }

    private void prepareForRead(StringBuilder hqlBuilder) {
        hqlBuilder.append("select ");
    }

    private void prepareForUpdate(String className, StringBuilder hqlBuilder) {
        hqlBuilder
                .append("update ")
                .append(className)
                .append(' ')
                .append(tl)
                .append(" set ");
    }

    private void select(EntityTable t, String abb, String[] as, StringBuilder hqlBuilder) {
        String[] fieldNames = t.getFieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            hqlBuilder
                    .append(abb)
                    .append('.')
                    .append(fieldNames[i])
                    .append(" as ")
                    .append(as[i])
                    .append(',');
        }
    }

    private void set(String name, StringBuilder hqlBuilder, String abb) {
        hqlBuilder
                .append(tl)
                .append('.')
                .append(name)
                .append("=:")
                .append(abb)
                .append(',');
    }

    private void from(Table t, StringBuilder hqlBuilder) {
        hqlBuilder
                .append(" from ")
                .append(t.getClassName())
                .append(' ')
                .append(tl)
                .append(' ');
    }

    private void join(NestedEntity table, String abb, StringBuilder hqlBuilder) {
        hqlBuilder
                .append("inner join ")
                .append(tl)
                .append('.')
                .append(table.getNestedEntityName())
                .append(" as ")
                .append(abb)
                .append(' ');
    }

    private void where(Where where, StringBuilder hqlBuilder) {
        hqlBuilder.
                append("where ")
                .append(tl)
                .append('.')
                .append(where.getFieldName())
                .append(where.getOperator().label)
                .append(':')
                .append(DEFAULT_WHERE_PARAM_NAME);
    }

    private void removeTrash(StringBuilder hqlBuilder) {
        hqlBuilder.deleteCharAt(hqlBuilder.length() - 1);
    }

}