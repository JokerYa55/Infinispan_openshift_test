/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest_exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author vasiliy.andricov
 */
public class ExNotDataFoundException extends AbstractWebApplicationException {

    public ExNotDataFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).
                header(HEADER_CACHE_CONTROL_NAME, HEADER_CACHE_CONTROL_VAL).
                header(HEADER_PRAGMA_NAME, HEADER_PRAGMA_VAL).
                entity(genMessage(message)).type(MediaType.APPLICATION_JSON).
                build());
    }

}
