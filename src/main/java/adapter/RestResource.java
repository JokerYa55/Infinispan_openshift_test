/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adapter;

import app.AppConst;
import static app.AppConst.LOG_HEADER_FORMAT_STR;
import static app.AppConst.PROPERTIES;
import static app.AppConst.getCache;
import static app.AppConst.getCurrentMethodName;
import static app.AppConst.getIp;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import javax.ws.rs.container.Suspended;
import java.util.logging.Level;
import org.infinispan.Cache;

/**
 *
 * @author vasil
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/rest_test", description = "REST TEST")
public class RestResource {

    private static final Logger LOG = Logger.getLogger(RestResource.class);
    private final String HEADER_CACHE_CONTROL_NAME = "Cache-Control";
    private final String HEADER_CACHE_CONTROL_VAL = "no-store";
    private final String HEADER_PRAGMA_NAME = "Pragma";
    private final String HEADER_PRAGMA_VAL = "no-cache";

    @Context
    private HttpHeaders requestHeaders;
    @Context
    private Response response;
    @Context
    private Request request;
    private static ConcurrentHashMap<String, Object> cash = new ConcurrentHashMap();

    //@Inject
    //user user_1;
    /**
     * Конструктор
     */
    public RestResource() {

    }

    /**
     * Получаение информации о пользователе
     *
     * @param p_realm
     * @return
     * @throws ParseException
     */
    @Path("/test")
    @GET
    @ApiOperation(value = "test", notes = "test")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")
        ,
        @ApiResponse(code = 500, message = "Something wrong in Server")})
    public Response test() throws ParseException, UnknownHostException, SocketException {
        String methodName = getCurrentMethodName();
        LOG.info(String.format(LOG_HEADER_FORMAT_STR, methodName));
        Cache<String, Object> cache = getCache();
        cache.put("app_base_url_" + PROPERTIES.get("app_number"), /*PROPERTIES.get("app_base_url") + " ip = " +*/ getIp());
        final Map<String, Object> resultMap = new HashMap<>();
        cache.keySet().forEach((t) -> {
            resultMap.put(t, cache.get(t));
        });
        return Response.status(Status.OK).entity(resultMap).build();
    }

    @Path("/async")
    @GET
    public void asyncGet(@Suspended final AsyncResponse asyncResponse) {
        LOG.info("ASYNC");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = veryExpensiveOperation();
                asyncResponse.resume(result);
            }

            private String veryExpensiveOperation() {
                long b_time = new Date().getTime();
                int j = 0;
                try {
                    Thread.sleep(300);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(RestResource.class.getName()).log(Level.SEVERE, null, ex);
                }
                long e_time = new Date().getTime();
                return "Very Expensive Operation = " + (e_time - b_time) + "   " + j;
            }
        }).start();
    }

    /**
     *
     * @param p_status
     * @param p_message
     * @return
     */
    private Response genResponse(Status p_status, Object p_message) {
        return Response.status(p_status).
                header(HEADER_CACHE_CONTROL_NAME, HEADER_CACHE_CONTROL_VAL).
                header(HEADER_PRAGMA_NAME, HEADER_PRAGMA_VAL).
                entity(p_message).
                build();
    }

    /**
     *
     * @param p_status
     * @param p_message
     * @return
     */
    private Response genResponse(int p_status, Object p_message) {
        return Response.status(p_status).
                header(HEADER_CACHE_CONTROL_NAME, HEADER_CACHE_CONTROL_VAL).
                header(HEADER_PRAGMA_NAME, HEADER_PRAGMA_VAL).
                entity(p_message).
                build();
    }

}
