package qa.logger;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Stack;

/**
 * tree logger.
 *  # TestClassName
 *   ├── < trace 1 >
 *   └── # nested class
 *       ├── < trace 2 >
 *       ├── < trace 3 >
 *       └── < trace 4 >
 */
public abstract class LogTraceTreePrinter {

    private static final String     BLUE                = "\u001B[34m";
    private static final String     WHITE               = "\033[0;97m";
    private static final String     CYAN                = "\033[0;96m";

    private static final char       DOWN                = '│';
    private static final String     RIGHT               = "──";
    private static final char       DOWN_RIGHT          = '└';
    private static final char       DOWN_RIGHT_DOWN     = '├';
    private static final String     IDENT               = "    ";
    private static final String     SMALL_IDENT         = "   ";

    private final PrintWriter       pw  =   new PrintWriter(System.out, false);
    private final StringBuilder     sb  =   new StringBuilder();

    protected void start(final Clazz clazz) {
        sb
                .append(WHITE)
                .append("# ")
                .append(BLUE)
                .append(clazz)
                .append(WHITE)
                .append('\n');
    }

    protected void clazz(final Map<Integer, Clazz> map, final Clazz clazz) {
        this.beforeClazz(map, clazz);
        this.writeClazzName(clazz);
    }

    protected void trace(final Map<Integer, Clazz> map, final Clazz clazz, final String message) {
        this.beforeTrace(map, clazz);
        this.writeMessage(message);
    }

    protected void print() {
        this.pw.write(sb.toString());
        this.pw.flush();
        this.sb.setLength(0);
    }

    private void beforeTrace(final Map<Integer, Clazz> map, final Clazz clazz) {
        final Integer key = clazz.getParent();

        this.writeParentParentLines(map, key);
        this.writeMethodLine(clazz);
    }

    private void beforeClazz(final Map<Integer, Clazz> map, final Clazz clazz) {
        final Clazz parent = map.get(clazz.getParent());
        final Integer key = parent.getParent();

        this.writeParentParentLines(map, key);
        this.writeParentClazzLine(parent);
    }

    private void writeMethodLine(final Clazz clazz) {
        if (clazz.getTestCount() == clazz.getTestPassed() && clazz.getClassCount() == clazz.getClassPassed())
            this.sb.append(DOWN_RIGHT);
        else
            this.sb.append(DOWN_RIGHT_DOWN);
    }

    private void writeParentClazzLine(final Clazz parent) {
        if (parent.getClassCount() == parent.getClassPassed())
            this.sb.append(DOWN_RIGHT);
        else
            this.sb.append(DOWN_RIGHT_DOWN);
    }

    private void writeParentParentLines(final Map<Integer, Clazz> map, final Integer key) {
        final Stack<Boolean> instructions = getInstructionsStack(map, key);
        this.writeInstructions(instructions);
    }

    private Stack<Boolean> getInstructionsStack(final Map<Integer, Clazz> map, Integer key) {
        final Stack<Boolean> stack = new Stack<>();
        while (map.containsKey(key)) {
            final Clazz clz = map.get(key);
            stack.push(clz.getClassCount() == clz.getClassPassed());
            key = map.get(key).getParent();
        }
        return stack;
    }

    private void writeInstructions(final Stack<Boolean> stack) {
        final int size = stack.size();
        for (int i = 0; i < size; i++) {
            if (stack.pop())
                this.sb
                        .append(IDENT);
            else
                this.sb
                        .append(DOWN)
                        .append(SMALL_IDENT);
        }
    }

    private void writeClazzName(final Clazz clazz) {
        this.sb
                .append(RIGHT)
                .append(" # ")
                .append(BLUE)
                .append(clazz)
                .append(WHITE)
                .append('\n');
    }

    private void writeMessage(final String message) {
        this.sb
                .append(RIGHT)
                .append(CYAN)
                .append(" < ")
                .append(WHITE)
                .append(message)
                .append(CYAN)
                .append(" >")
                .append(WHITE)
                .append('\n');

    }
}
