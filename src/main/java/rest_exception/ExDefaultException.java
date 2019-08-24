/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest_exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static rest_exception.AbstractWebApplicationException.HEADER_CACHE_CONTROL_NAME;

/**
 *
 * @author vasiliy.andricov
 */
public class ExDefaultException extends AbstractWebApplicationException {

    public ExDefaultException(int p_status_code, String message) {
        super(Response.status(p_status_code).
                header(HEADER_CACHE_CONTROL_NAME, HEADER_CACHE_CONTROL_VAL).
                header(HEADER_PRAGMA_NAME, HEADER_PRAGMA_VAL).
                entity(genMessage(message)).type(MediaType.APPLICATION_JSON).
                build());
    }

    public ExDefaultException(String message) {
        super(message);
    }

}
