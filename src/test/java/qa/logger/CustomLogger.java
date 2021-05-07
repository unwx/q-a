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
        this.getClazzData(clazz);
        this.clazz(clazz);
        this.currentClazz = clazz;
        this.clazzCount = hashToClazzMap.size();
    }

    protected void nested(final Class<?> clazz) {
        final Clazz clz = hashToClazzMap.get(System.identityHashCode(clazz));
        this.incrementParentCounter(hashToClazzMap, clz);
        super.clazz(hashToClazzMap, clz);
        this.currentClazz = clazz;
    }

    protected void trace(final String message) {
        final Clazz clazz = hashToClazzMap.get(System.identityHashCode(currentClazz));
        clazz.incrementTestPassed();
        super.trace(hashToClazzMap,
                clazz,
                message);
    }

    protected void print() {
        super.print();
    }

    private void clazz(final Class<?> clazz) {
        super.start(hashToClazzMap.get(System.identityHashCode(clazz)));
    }

    private void getClazzData(final Class<?> clazz) {
        final Stack<Class<?>> stack = new Stack<>();
        this.pushStartClass(stack, clazz);

        while (stack.size() != 0) {
            spreadClass(stack);
        }
    }

    private void spreadClass(final Stack<Class<?>> stack) {
        final Class<?> target = stack.pop();
        final Class<?>[] nested = target.getDeclaredClasses();

        for (Class<?> clz : nested) {
            stack.push(clz);

            final Clazz clazz = new Clazz(classNestedCount(clz),
                    this.classTestCount(clz),
                    System.identityHashCode(target),
                    clz.getSimpleName());
            this.hashToClazzMap.put(System.identityHashCode(clz), clazz);
        }
    }

    private void pushStartClass(final Stack<Class<?>> stack, final Class<?> clazz) {
        stack.push(clazz);
        final Clazz target = new Clazz(classNestedCount(clazz),
                this.classTestCount(clazz),
                null,
                clazz.getSimpleName());
        this.hashToClazzMap.put(System.identityHashCode(clazz), target);
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
