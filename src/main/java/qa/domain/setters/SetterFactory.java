package qa.domain.setters;

import java.lang.invoke.*;

public class SetterFactory {
    @SuppressWarnings("unchecked")
    public static <T> ISetter<T> getSetter(Class<T> clazz, String fieldName, Class<?> fieldType) throws Throwable {

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

        return (ISetter<T>) factory.invoke();
    }

    private static String computeSetterName(String name) {
        return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
