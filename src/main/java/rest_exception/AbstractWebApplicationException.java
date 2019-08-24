/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest_exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author vasiliy.andricov
 */
public class AbstractWebApplicationException extends WebApplicationException {

    protected final static String HEADER_CACHE_CONTROL_NAME = "Cache-Control";
    protected final static String HEADER_CACHE_CONTROL_VAL = "no-store";
    protected final static String HEADER_PRAGMA_NAME = "Pragma";
    protected final static String HEADER_PRAGMA_VAL = "no-cache";

    public AbstractWebApplicationException(Response response) {
        super(response);
    }

    public AbstractWebApplicationException(String message) {
        super(message);
    }

    protected static String genMessage(String message) {
        JSONObject json = new JSONObject();
        json.put("errorMessage", message);
        return json.toJSONString();
    }
}
