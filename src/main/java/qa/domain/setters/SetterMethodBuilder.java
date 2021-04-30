package qa.domain.setters;

import qa.dao.database.components.FieldDataSetterExtractor;

import java.lang.invoke.*;

public final class SetterMethodBuilder {

    private SetterMethodBuilder() {
    }

    @SuppressWarnings("unchecked")
    public static ISetter<FieldDataSetterExtractor> getSetter(Class<? extends FieldDataSetterExtractor> clazz, String fieldName, Class<?> fieldType) throws Throwable {

        MethodHandles.Lookup caller = MethodHandles.lookup();
        MethodType setter = MethodType.methodType(void.class, fieldType);
        MethodHandle target = caller.findVirtual(clazz, computeSetterName(fieldName), setter);
        MethodType func = target.type();

        CallSite site = LambdaMetafactory.metafactory(
                caller,
                "set",
                MethodType.methodType(ISetter.class),
                func.erase(),
                target,
                func
        );

        MethodHandle factory = site.getTarget();

        return (ISetter<FieldDataSetterExtractor>) factory.invoke();
    }

    private static String computeSetterName(String name) {
        return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
