package qa.logger;

import org.jetbrains.annotations.Nullable;

public class Clazz {

    @Nullable
    private final Integer parent;
    private final String name;

    private final int classCount;
    private int classPassed;

    private final int testCount;
    private int testPassed;

    public Clazz(int classCount,
                 int testCount,
                 @Nullable Integer parent,
                 String name) {
        this.classCount = classCount;
        this.parent = parent;
        this.name = name;
        this.testCount = testCount;
        this.classPassed = 0;
        this.testPassed = 0;
    }

    public String getName() {
        return name;
    }

    public int getClassCount() {
        return classCount;
    }

    public int getClassPassed() {
        return classPassed;
    }

    public int getTestCount() {
        return testCount;
    }

    public int getTestPassed() {
        return testPassed;
    }

    public @Nullable Integer getParent() {
        return parent;
    }

    public void incrementClassPassed() {
        this.classPassed++;
    }

    public void incrementTestPassed() {
        this.testPassed++;
    }

    @Override
    public String toString() {
        return name;
    }
}
