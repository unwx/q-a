package qa.util;

import qa.exceptions.rest.ResourceNotFoundException;

public class ResourceUtil {

    private ResourceUtil() {
    }

     public static <T> T throwResourceNFExceptionIfNull(T target, String message) {
        if (target == null)
            throw new ResourceNotFoundException(message);
        return target;
     }
}
