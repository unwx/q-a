package qa.logger;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * emphasis on performance
 */
public abstract class CustomLogger extends LogTraceTreePrinter {

    private final Map<Integer, Clazz> hashToClazzMap = new HashMap<>();
    private Class<?> currentClazz;
    private final int clazzCount;

    public CustomLogger(final Class<?> clazz) {
        getClazzData(clazz);
        clazz(clazz);
        this.currentClazz = clazz;
        this.clazzCount = hashToClazzMap.size();
    }

    protected void nested(final Class<?> clazz) {
        final Clazz clz = hashToClazzMap.get(System.identityHashCode(clazz));
        incrementParentCounter(hashToClazzMap, clz);
        super.clazz(hashToClazzMap, clz);
        currentClazz = clazz;
    }

    protected void trace(final String message) {
        final Clazz clazz = hashToClazzMap.get(System.identityHashCode(currentClazz));
        clazz.incrementTestPassed();
        super.trace(hashToClazzMap,
                clazz,
                message);
    }

    protected void end() {
        super.end();
    }

    private void clazz(final Class<?> clazz) {
        super.start(hashToClazzMap.get(System.identityHashCode(clazz)));
    }

    private void getClazzData(final Class<?> clazz) {
        final Stack<Class<?>> stack = new Stack<>();
        pushStartClass(stack, clazz);

        while (stack.size() != 0) {
            spreadClass(stack);
        }
    }

    private void spreadClass(final Stack<Class<?>> stack) {
        final Class<?> target = stack.pop();
        final Class<?>[] nested = target.getDeclaredClasses();

        for (Class<?> clz : nested) {
            stack.push(clz);
            hashToClazzMap.put(
                    System.identityHashCode(clz),
                    new Clazz(classNestedCount(clz),
                            classTestCount(clz),
                            System.identityHashCode(target),
                            clz.getSimpleName()));
        }
    }

    private void pushStartClass(final Stack<Class<?>> stack, final Class<?> clazz) {
        stack.push(clazz);
        hashToClazzMap.put(System.identityHashCode(clazz),
                new Clazz(classNestedCount(clazz),
                        classTestCount(clazz),
                        null,
                        clazz.getSimpleName()));
    }

    private int classTestCount(Class<?> clazz) {
        int counter = 0;
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getAnnotation(Test.class) != null)
                counter++;
        }
        return counter;
    }

    private int classNestedCount(final Class<?> clazz) {
        return clazz.getDeclaredClasses().length;
    }

    private void incrementParentCounter(final Map<Integer, Clazz> map, final Clazz clazz) {
        map.get(clazz.getParent()).incrementClassPassed();
    }

    protected int getClazzCount() {
        return clazzCount;
    }
}
