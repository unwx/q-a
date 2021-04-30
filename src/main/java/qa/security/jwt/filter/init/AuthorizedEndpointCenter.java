package qa.security.jwt.filter.init;

import qa.rest.*;

import java.util.*;

public class AuthorizedEndpointCenter {

    private static final Set<String> userSet = new HashSet<>();
    private static final List<String> parameterizedUserList = new LinkedList<>();

    public static void init() {
        final List<Class<?>> controllers = new ArrayList<>(6);
        controllers.add(AnswerRestController.class);
        controllers.add(AuthenticationRestController.class);
        controllers.add(CommentAnswerRestController.class);
        controllers.add(CommentQuestionRestController.class);
        controllers.add(QuestionRestController.class);
        controllers.add(UserRestController.class);

        final AuthorizedEndpointLoader loader = new AuthorizedEndpointLoader(controllers);
        final Stack<String> endpoints = loader.compute();

        assert endpoints != null;
        while (endpoints.size() > 0) {
            final String endpoint = endpoints.pop();

            final int parameters = getParameterizedEndpoint(endpoint);
            if (parameters > 0)
                parameterizedUserList.add(cleanupEndpoint(endpoint, parameters));
            else
                userSet.add(endpoint);
        }
    }

    public static boolean contains(String uri) {
        if (userSet.contains(uri))
            return true;

        for (String s : parameterizedUserList) {
            if (uri.startsWith(s))
                return true;
        }
        return false;
    }

    private static int getParameterizedEndpoint(String endpoint) {
        if (endpoint.contains("{")) {
            int counter = 0;
            for (int i = 0; i < endpoint.length(); i++) {
                if (endpoint.charAt(i) == '{')
                    counter++;
            }
            return counter;
        }
        return 0;
    }

    private static String cleanupEndpoint(String endpoint, int parameters) {
        for (int i = endpoint.length() - 1; i > 0; i--) {
            if (endpoint.charAt(i) == '{') {
                parameters--;
            }

            if (parameters == 0 && endpoint.charAt(i) == '/') {
                return endpoint.substring(0, i);
            }
        }
        return endpoint;
    }
}
