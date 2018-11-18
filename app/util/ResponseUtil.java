package util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class ResponseUtil {

    public static ObjectNode createResponse(Object response, boolean ok) {

        ObjectNode result = Json.newObject();

        result.put("isSuccessfull", ok);

        if (response instanceof String) {
            result.put("body", (String) response);
        } else {
            result.set("body", result);
        }

        return result;
    }
}
