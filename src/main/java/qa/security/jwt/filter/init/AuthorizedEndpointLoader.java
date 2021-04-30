package qa.security.jwt.filter.init;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serial;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

public final class AuthorizedEndpointLoader extends RecursiveTask<Stack<String>> {

    @Serial
    private static final long serialVersionUID = -5846813608103280L;

    private final List<Class<?>> controllers;
    private static final int THRESHOLD = 10;

    public AuthorizedEndpointLoader(List<Class<?>> controllers) {
        this.controllers = controllers;
    }

    @Override
    protected Stack<String> compute() {
        if (controllers.size() > THRESHOLD) {
            final ArrayList<AuthorizedEndpointLoader> tasks = subtasks();
            final AuthorizedEndpointLoader one = tasks.get(0);
            final AuthorizedEndpointLoader two = tasks.get(1);

            one.fork();
            final Stack<String> result = two.compute();
            one.join().addAll(result);

            try {
                return one.get();
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }

        } else {
            final Stack<String> result = new Stack<>();
            for (Class<?> controller : controllers) {
                result.addAll(this.process(controller));
            }
            return result;
        }
    }

    private ArrayList<AuthorizedEndpointLoader> subtasks() {
        final ArrayList<AuthorizedEndpointLoader> subtasks = new ArrayList<>(2);

        final AuthorizedEndpointLoader one = new AuthorizedEndpointLoader(controllers.subList(0, controllers.size() / 2));
        final AuthorizedEndpointLoader two = new AuthorizedEndpointLoader(controllers.subList(controllers.size() / 2, controllers.size()));

        subtasks.add(one);
        subtasks.add(two);

        return subtasks;
    }

    private Stack<String> process(Class<?> controller) {
        final Stack<String> endpoints = new Stack<>();
        final Method[] methods = controller.getDeclaredMethods();

        final String controllerMapping = resolveControllerMapping(controller);

        for (Method m : methods) {
            final PreAuthorize preAuthorize = m.getAnnotation(PreAuthorize.class);
            if (preAuthorize == null) continue;

            if (preAuthorize.value().equals("hasAuthority('USER')")) {
                final RequestMapping requestMapping = m.getAnnotation(RequestMapping.class);
                if (requestMapping == null) continue;

                endpoints.push(controllerMapping + requestMapping.value()[0]);
            }
        }
        return endpoints;
    }

    private String resolveControllerMapping(Class<?> controller) {
        final RequestMapping requestMapping = controller.getAnnotation(RequestMapping.class);
        if (requestMapping == null) return "";
        return requestMapping.value()[0];
    }
}
