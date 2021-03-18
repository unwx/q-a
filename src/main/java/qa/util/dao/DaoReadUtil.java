package qa.util.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.HqlBuilder;
import qa.dao.databasecomponents.*;
import qa.domain.setters.PropertySetter;

import javax.persistence.NoResultException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DaoReadUtil<Entity extends FieldExtractor & FieldDataSetterExtractor> {

    private final HqlBuilder<Entity> hqlBuilder;
    private final PropertySetter mainSetter;
    private final Entity targetEntity;

    public DaoReadUtil(HqlBuilder<Entity> hqlBuilder,
                       Entity emptyEntity,
                       PropertySetter propertySetter) {
        this.hqlBuilder = hqlBuilder;
        this.targetEntity = emptyEntity;
        this.mainSetter = propertySetter;
    }

    public Entity read(final Where where,
                       final Table target,
                       final List<NestedEntity> nested,
                       final Session session)
            throws
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {

        return readUniqueProcess(where, target, nested, session, targetEntity);
    }

    public List<Entity> readList(final Where where,
                                 final Table mainTable,
                                 final Session session) {
        return readListProcess(where, mainTable, session, targetEntity);
    }

    private Entity readUniqueProcess(Where where,
                                     Table mainTable,
                                     List<NestedEntity> nestedEntities,
                                     Session session,
                                     Entity targetEntity)
            throws
            InvocationTargetException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException {

        ReadSituation situation = defineSituation(mainTable.getFieldNames(), nestedEntities);
        Object whereParameterValue = where.getFieldValue();
        Entity result = null;

        session.beginTransaction();
        if (situation != ReadSituation.MAIN_NESTED_MANY_OBJECTS) {
            String hql = hqlBuilder.read(where, mainTable, nestedEntities);
            Query<?> query = session.createQuery(hql).setParameter(hqlBuilder.DEFAULT_WHERE_PARAM_NAME, whereParameterValue);

            switch (situation) {
                case MAIN_SINGLE_OBJECT -> result = readMainSingle(
                        query,
                        mainTable.getFieldNames()[0],
                        targetEntity);
                case MAIN_MANY_OBJECTS -> result = readMainMany(
                        query,
                        mainTable.getFieldNames(),
                        targetEntity);
                case NESTED_SINGLE_OBJECT -> result = readNestedSingle(
                        query,
                        nestedEntities.get(0),
                        targetEntity);
                case NESTED_MANY_OBJECTS -> result = readNestedMany(
                        query,
                        nestedEntities,
                        targetEntity);
                case MAIN_NESTED_SINGLE_OBJECT -> result = readMainNestedSingle(
                        query,
                        mainTable.getFieldNames()[0],
                        nestedEntities.get(0),
                        targetEntity);
            }
        } else {
            String mainHql = hqlBuilder.read(where, mainTable, Collections.emptyList());
            String nestedHql = hqlBuilder.read(where, new Table(new String[]{}, mainTable.getClassName()), nestedEntities);
            Query<?> nestedQuery = session.createQuery(nestedHql).setParameter(hqlBuilder.DEFAULT_WHERE_PARAM_NAME, whereParameterValue);
            Query<?> mainQuery = session.createQuery(mainHql).setParameter(hqlBuilder.DEFAULT_WHERE_PARAM_NAME, whereParameterValue);

            result = readMainNestedMany(
                    mainQuery,
                    nestedQuery,
                    mainTable.getFieldNames(),
                    nestedEntities,
                    targetEntity);
        }
        session.getTransaction().commit();
        return result;
    }

    private List<Entity> readListProcess(Where where,
                                         Table mainTable,
                                         Session session,
                                         Entity targetEntity) {
        String hql = hqlBuilder.read(where, mainTable, Collections.emptyList());
        Query<?> query = session.createQuery(hql).setParameter(hqlBuilder.DEFAULT_WHERE_PARAM_NAME, where.getFieldValue());
        List<Entity> result;
        session.beginTransaction();
        if (mainTable.getFieldNames().length == 1)
            result = readMainSingleList(query, mainTable.getFieldNames()[0], targetEntity);
        else result = readMainManyList(query, mainTable.getFieldNames(), targetEntity);
        session.getTransaction().commit();
        return result;
    }

    private Entity readMainSingle(Query<?> query,
                                  String mainFieldName,
                                  Entity targetEntity) {
        try {
            Object result = query.getSingleResult();
            return setProperty(mainFieldName, result, targetEntity);
        } catch (NoResultException e) {
            return null;
        }
    }

    private Entity readMainMany(Query<?> query,
                                String[] mainFieldNames,
                                Entity targetEntity) {
        try {
            Object[] result = (Object[]) query.getSingleResult();
            return setProperties(mainFieldNames, result, targetEntity);
        } catch (NoResultException e) {
            return null;
        }
    }

    private Entity readNestedSingle(Query<?> query,
                                    NestedEntity nestedEntity,
                                    Entity targetEntity)
            throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        try {
            Object result = query.getSingleResult();
            setNested(
                    nestedEntity,
                    targetEntity,
                    nestedEntity.getClazz().getDeclaredConstructor().newInstance(),
                    nestedEntity.getFieldNames()[0],
                    result);
            return targetEntity;
        } catch (NoResultException e) {
            return null;
        }
    }

    private Entity readNestedMany(Query<?> query,
                                  List<NestedEntity> nestedEntity,
                                  Entity targetEntity)
            throws
            InvocationTargetException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException {

        query.setMaxResults(50);
        List<?> result = query.list();

        /* we want to avoid the situation when it would seem that an entity is returned,
         *that is, the result - but in the end the entities nested in it are equal to null.
         * @Nullable */
        if (result.isEmpty())
            return null;
        setNestedResultListToEntity(result, targetEntity, nestedEntity);
        return targetEntity; // updated;
    }

    private Entity readMainNestedSingle(Query<?> query,
                                        String mainFieldName,
                                        NestedEntity nestedEntity,
                                        Entity targetEntity)
            throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        try {
            Object[] result = (Object[]) query.getSingleResult();
            setProperty(mainFieldName, result[0], targetEntity);
            setNested(nestedEntity, targetEntity, nestedEntity.getClazz().getDeclaredConstructor().newInstance(), nestedEntity.getFieldNames()[0], result[1]);
            return targetEntity;
        } catch (NoResultException e) {
            return null;
        }
    }

    private Entity readMainNestedMany(Query<?> mainQuery,
                                      Query<?> nestedQuery,
                                      String[] mainFieldNames,
                                      List<NestedEntity> nestedEntities,
                                      Entity targetEntity)
            throws
            InvocationTargetException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException {

        try {
            Object[] mainResult = (Object[]) mainQuery.getSingleResult();
            setProperties(mainFieldNames, mainResult, targetEntity);

            List<?> nestedResult = nestedQuery.list();
            setNestedResultListToEntity(nestedResult, targetEntity, nestedEntities);
            return targetEntity;
        } catch (NoResultException e) {
            return null;
        }
    }

    private List<Entity> readMainSingleList(Query<?> query,
                                            String mainFieldName,
                                            Entity targetEntity) {

        query.setMaxResults(50);
        List<?> result = query.list();
        List<Entity> entities = new LinkedList<>();
        result.forEach((o) -> {
            mainSetter.set(targetEntity, mainFieldName, o);
            entities.add(targetEntity);
        });
        return entities;
    }

    @SuppressWarnings("unchecked")
    private List<Entity> readMainManyList(Query<?> query,
                                          String[] mainFieldNames,
                                          Entity targetEntity) {

        query.setMaxResults(50);
        List<Object[]> result = (List<Object[]>) query.list();
        List<Entity> entities = new LinkedList<>();
        result.forEach((o) -> {
            for (int i = 0; i < mainFieldNames.length; i++) {
                mainSetter.set(targetEntity, mainFieldNames[i], o[i]);
            }
            entities.add(targetEntity);
        });
        return entities;
    }

    private void setNestedResultListToEntity(List<?> resultList, Entity to, List<NestedEntity> nestedEntities)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (int i = 0; i < resultList.size(); i++) {
            if (nestedEntities.get(i).getFieldNames().length == 1) {
                Object result = resultList.get(i);
                NestedEntity e = nestedEntities.get(i);
                setNested(e, to, e.getClazz().getDeclaredConstructor().newInstance(), e.getFieldNames()[0], result);
            } else {
                Object[] result = (Object[]) resultList.get(i);
                NestedEntity e = nestedEntities.get(i);
                setNested(e, to, e.getClazz().getDeclaredConstructor().newInstance(), e.getFieldNames(), result);
            }
        }
    }

    private Entity setProperty(String mainFieldName,
                               Object mainFieldValue,
                               Entity entity) {
        mainSetter.set(entity, mainFieldName, mainFieldValue);
        return entity;
    }

    private FieldDataSetterExtractor setProperty(String mainFieldName,
                                                 Object mainFieldValue,
                                                 FieldDataSetterExtractor obj,
                                                 PropertySetter setter) {
        setter.set(obj, mainFieldName, mainFieldValue);
        return obj;
    }

    private Entity setProperties(String[] mainFieldNames,
                                 Object[] mainFieldValues,
                                 Entity entity) {
        mainSetter.setAll(entity, mainFieldNames, mainFieldValues);
        return entity;
    }

    private FieldDataSetterExtractor setProperties(String[] mainFieldNames,
                                                   Object[] mainFieldValues,
                                                   FieldDataSetterExtractor obj,
                                                   PropertySetter setter) {
        setter.setAll(obj, mainFieldNames, mainFieldValues);
        return obj;
    }

    private void setNested(NestedEntity nestedEntity,
                           Entity target,
                           FieldDataSetterExtractor nested,
                           String name,
                           Object value) {
        FieldDataSetterExtractor data = setProperty(
                name,
                value,
                nested,
                nestedEntity.getDomainSetter());
        mainSetter.set(target, nestedEntity.getNestedEntityName(), data);
    }

    private void setNested(NestedEntity nestedEntity,
                           Entity target,
                           FieldDataSetterExtractor nested,
                           String[] names,
                           Object[] values) {
        FieldDataSetterExtractor data = setProperties(
                names,
                values,
                nested,
                nestedEntity.getDomainSetter());
        mainSetter.set(target, nestedEntity.getNestedEntityName(), data);
    }

    private ReadSituation defineSituation(String[] mainFieldNames,
                                          List<NestedEntity> nested) {
        if (nested.size() == 0) {
            if (mainFieldNames.length == 1)
                return ReadSituation.MAIN_SINGLE_OBJECT;
            return ReadSituation.MAIN_MANY_OBJECTS;
        }
        if (mainFieldNames.length == 0) {
            if (nested.size() == 1 && nested.get(0).getFieldNames().length == 1)
                return ReadSituation.NESTED_SINGLE_OBJECT;
            return ReadSituation.NESTED_MANY_OBJECTS;
        }
        if (nested.size() == 1 && mainFieldNames.length == 1)
            return ReadSituation.MAIN_NESTED_SINGLE_OBJECT;
        return ReadSituation.MAIN_NESTED_MANY_OBJECTS;
    }
}
