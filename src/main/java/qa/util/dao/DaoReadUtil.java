package qa.util.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import qa.dao.Domain;
import qa.dao.HqlBuilder;
import qa.dao.database.components.*;
import qa.domain.setters.PropertySetter;

import javax.persistence.NoResultException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DaoReadUtil<E extends FieldExtractor & FieldDataSetterExtractor & Domain> {

    private final HqlBuilder hqlBuilder = new HqlBuilder();
    private final PropertySetter mainSetter;

    private static final Logger logger = LogManager.getLogger(DaoReadUtil.class);

    public DaoReadUtil(PropertySetter propertySetter) {
        this.mainSetter = propertySetter;
    }

    public E read(final Where where,
                  final Table target,
                  final List<NestedEntity> nested,
                  final Session session)
            throws
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {

        return this.readUniqueProcess(where, target, nested, session);
    }

    public List<E> readList(final Where where,
                            final Table mainTable,
                            final Session session) {
        return this.readListProcess(where, mainTable, session);
    }

    private E readUniqueProcess(Where where,
                                Table mainTable,
                                List<NestedEntity> nestedEntities,
                                Session session)
            throws
            InvocationTargetException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException {

        final ReadSituation situation = this.defineSituation(mainTable.getFieldNames(), nestedEntities);
        final Object whereParameterValue = where.getFieldValue();
        E result = null;

        session.beginTransaction();
        if (situation != ReadSituation.MAIN_NESTED_MANY_OBJECTS) {
            final String hql = hqlBuilder.read(where, mainTable, nestedEntities);
            final Query<?> query = session.createQuery(hql).setParameter(hqlBuilder.DEFAULT_WHERE_PARAM_NAME, whereParameterValue);

            switch (situation) {
                case MAIN_SINGLE_OBJECT -> result = readMainSingle(
                        query,
                        mainTable.getFieldNames()[0]
                );
                case MAIN_MANY_OBJECTS -> result = readMainMany(
                        query,
                        mainTable.getFieldNames()
                );
                case NESTED_SINGLE_OBJECT -> result = readNestedSingle(
                        query,
                        nestedEntities.get(0)
                );
                case NESTED_MANY_OBJECTS -> result = readNestedMany(
                        query,
                        nestedEntities
                );
                case MAIN_NESTED_SINGLE_OBJECT -> result = readMainNestedSingle(
                        query,
                        mainTable.getFieldNames()[0],
                        nestedEntities.get(0)
                );
                default -> logger.error("uncertain situation");
            }

        } else {
            final String mainHql = this.hqlBuilder.read(where, mainTable, Collections.emptyList());
            final String nestedHql = this.hqlBuilder.read(where, new Table(new String[]{}, mainTable.getClassName()), nestedEntities);
            final Query<?> nestedQuery = session.createQuery(nestedHql).setParameter(this.hqlBuilder.DEFAULT_WHERE_PARAM_NAME, whereParameterValue);
            final Query<?> mainQuery = session.createQuery(mainHql).setParameter(this.hqlBuilder.DEFAULT_WHERE_PARAM_NAME, whereParameterValue);

            result = readMainNestedMany(
                    mainQuery,
                    nestedQuery,
                    mainTable.getFieldNames(),
                    nestedEntities
            );
        }

        session.getTransaction().commit();
        return result;
    }

    private List<E> readListProcess(Where where,
                                    Table mainTable,
                                    Session session) {

        final String hql = this.hqlBuilder.read(where, mainTable, Collections.emptyList());
        final Query<?> query = session.createQuery(hql).setParameter(this.hqlBuilder.DEFAULT_WHERE_PARAM_NAME, where.getFieldValue());
        final List<E> result;
        session.beginTransaction();

        if (mainTable.getFieldNames().length == 1)
            result = readMainSingleList(query, mainTable.getFieldNames()[0]);
        else
            result = readMainManyList(query, mainTable.getFieldNames());

        session.getTransaction().commit();
        return result;
    }

    private E readMainSingle(Query<?> query,
                             String mainFieldName) {

        final E entity = this.mainSetter.entity();
        try {

            final Object result = query.getSingleResult();
            return setProperty(mainFieldName, result, entity);

        } catch (NoResultException e) {
            return null;
        }
    }

    private E readMainMany(Query<?> query,
                           String[] mainFieldNames) {

        final E entity = this.mainSetter.entity();
        try {

            final Object[] result = (Object[]) query.getSingleResult();
            return setProperties(mainFieldNames, result, entity);

        } catch (NoResultException e) {
            return null;
        }
    }

    private E readNestedSingle(Query<?> query,
                               NestedEntity nestedEntity)
            throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        final E entity = this.mainSetter.entity();
        try {
            final Object result = query.getSingleResult();
            this.setNested(
                    nestedEntity,
                    entity,
                    nestedEntity.getClazz().getDeclaredConstructor().newInstance(),
                    nestedEntity.getFieldNames()[0],
                    result);
            return entity;
        } catch (NoResultException e) {
            return null;
        }
    }

    private E readNestedMany(Query<?> query,
                             List<NestedEntity> nestedEntity)
            throws
            InvocationTargetException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException {

        final E entity = this.mainSetter.entity();
        query.setMaxResults(50);

        final List<?> result = query.list();

        /* we want to avoid the situation when it would seem that an entity is returned,
         *that is, the result - but in the end the entities nested in it are equal to null.
         * @Nullable */
        if (result.isEmpty())
            return null;
        this.setNestedResultListToEntity(result, entity, nestedEntity);
        return entity; // updated;
    }

    private E readMainNestedSingle(Query<?> query,
                                   String mainFieldName,
                                   NestedEntity nestedEntity)
            throws
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        final E entity = this.mainSetter.entity();
        try {

            final Object[] result = (Object[]) query.getSingleResult();
            this.setProperty(mainFieldName, result[0], entity);

            this.setNested(
                    nestedEntity,
                    entity,
                    nestedEntity
                            .getClazz()
                            .getDeclaredConstructor()
                            .newInstance(),
                    nestedEntity.getFieldNames()[0],
                    result[1]
            );

            return entity;

        } catch (NoResultException e) {
            return null;
        }
    }

    private E readMainNestedMany(Query<?> mainQuery,
                                 Query<?> nestedQuery,
                                 String[] mainFieldNames,
                                 List<NestedEntity> nestedEntities)
            throws
            InvocationTargetException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException {

        final E entity = this.mainSetter.entity();
        try {
            final Object[] mainResult = (Object[]) mainQuery.getSingleResult();
            this.setProperties(mainFieldNames, mainResult, entity);

            final List<?> nestedResult = nestedQuery.list();
            this.setNestedResultListToEntity(nestedResult, entity, nestedEntities);

            return entity;
        } catch (NoResultException e) {
            return null;
        }
    }

    private List<E> readMainSingleList(Query<?> query,
                                       String mainFieldName) {

        final E entity = this.mainSetter.entity();
        query.setMaxResults(50);

        final List<?> result = query.list();
        final List<E> entities = new LinkedList<>();

        for (Object object : result) {
            this.mainSetter.set(entity, mainFieldName,object);
            entities.add(entity);
        }

        return entities;
    }

    @SuppressWarnings("unchecked")
    private List<E> readMainManyList(Query<?> query,
                                     String[] mainFieldNames) {

        final E entity = this.mainSetter.entity();
        query.setMaxResults(50);

        final List<Object[]> result = (List<Object[]>) query.list();
        final List<E> entities = new LinkedList<>();

        for (Object[] objects : result) {

            for (int i = 0; i < mainFieldNames.length; i++) {
                this.mainSetter.set(entity, mainFieldNames[i],objects[i]);
            }

            entities.add(entity);
        }
        return entities;
    }

    private void setNestedResultListToEntity(List<?> resultList, E to, List<NestedEntity> nestedEntities)
            throws NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {

        for (int i = 0; i < resultList.size(); i++) {
            if (nestedEntities.get(i).getFieldNames().length == 1) {

                final Object result = resultList.get(i);
                final NestedEntity e = nestedEntities.get(i);
                this.setNested(e, to, e.getClazz().getDeclaredConstructor().newInstance(), e.getFieldNames()[0], result);


            } else {

                final Object[] result = (Object[]) resultList.get(i);
                final NestedEntity e = nestedEntities.get(i);
                this.setNested(e, to, e.getClazz().getDeclaredConstructor().newInstance(), e.getFieldNames(), result);

            }
        }
    }

    private E setProperty(String mainFieldName,
                          Object mainFieldValue,
                          E entity) {

        this.mainSetter.set(entity, mainFieldName, mainFieldValue);
        return entity;
    }

    private FieldDataSetterExtractor setProperty(String mainFieldName,
                                                 Object mainFieldValue,
                                                 FieldDataSetterExtractor obj,
                                                 PropertySetter setter) {

        setter.set(obj, mainFieldName, mainFieldValue);
        return obj;
    }

    private E setProperties(String[] mainFieldNames,
                            Object[] mainFieldValues,
                            E entity) {

        this.mainSetter.setAll(entity, mainFieldNames, mainFieldValues);
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
                           E target,
                           FieldDataSetterExtractor nested,
                           String name,
                           Object value) {

        final FieldDataSetterExtractor data = this.setProperty(
                name,
                value,
                nested,
                nestedEntity.getDomainSetter());
        this.mainSetter.set(target, nestedEntity.getTargetNestedFieldName(), data);
    }

    private void setNested(NestedEntity nestedEntity,
                           E target,
                           FieldDataSetterExtractor nested,
                           String[] names,
                           Object[] values) {

        final FieldDataSetterExtractor data = this.setProperties(
                names,
                values,
                nested,
                nestedEntity.getDomainSetter());
        this.mainSetter.set(target, nestedEntity.getTargetNestedFieldName(), data);
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
