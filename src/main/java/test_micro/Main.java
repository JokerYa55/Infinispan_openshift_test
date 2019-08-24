/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_micro;

import static app.AppConst.APP_PATH;
import static app.AppConst.APP_PROPERTY_LIST;
import static app.AppConst.PROPERTIES;
import com.wordnik.swagger.jaxrs.config.BeanConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;import java.util.HashMap;
;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.ext.ContextResolver;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.CompressionConfig;
import org.glassfish.grizzly.http.server.ErrorPageGenerator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author vasil
 */
public class Main {

    public static String base_url = "";
    private static final Logger LOG = Logger.getLogger(Main.class);    
    public static String username_pattern_exp = "^(?=.{1,50}$)[a-zA-Z0-9_.@-]+()$";       
    

    /**
     *
     * @return
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.dekses.jersey.docker.demo package
        final ResourceConfig rc = new ResourceConfig().packages("sso_b2c.adminrest_b2c_micro");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI             
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0");
        beanConfig.setScan(true);
        beanConfig.setResourcePackage(RestResource.class.getPackage().getName());
        beanConfig.setBasePath(base_url);
        beanConfig.setDescription("Admin rest b2c");
        beanConfig.setTitle("admin b2c API");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create((String) PROPERTIES.get("app_base_url")), createApp(), false);

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (getPropertiesFromFile(String.format("%s%s%s", APP_PATH, File.separator, "app.properties"))) {
            final HttpServer server = startServer();

            // включаем компрессию
            if (((String) PROPERTIES.get("app_response_compress_enables")).equals("true")) {
                LOG.info("response_compress enabled!");
                CompressionConfig compressionConfig = server.getListener("grizzly").getCompressionConfig();
                compressionConfig.setCompressionMode(CompressionConfig.CompressionMode.ON); // the mode
                compressionConfig.setCompressionMinSize(1); // the min amount of bytes to compress
                compressionConfig.setCompressableMimeTypes("text/plain", "text/html", "application/json"); // the mime types to compress
            }
            // Устанавливаем страницу ошибок
            server.getServerConfiguration().setDefaultErrorPageGenerator(new ErrorPageGenerator() {
                @Override
                public String generate(Request rqst, int i, String string, String string1, Throwable thrwbl) {
                    return String.format("Error"); //To change body of generated lambdas, choose Tools | Templates.
                }
            });
            server.start();
            LOG.info(String.format("\n******************************** \nApp started with WADL available at "
                    + "%sapplication.wadl\nHit enter to stop it...", (String) PROPERTIES.get("app_base_url")));
            LOG.info("*********** PROD ***************");
            Thread.currentThread().join();
        } else {
            throw new UnsupportedOperationException("file property not found");
        }
    }

    /**
     * Включаем swagger
     *
     * @return
     */
    public static ResourceConfig createApp() {
        return new ResourceConfig().
                packages(RestResource.class.getPackage().getName(),
                        "com.wordnik.swagger.jaxrs.listing").
                register(createMoxyJsonResolver());
    }

    /**
     * Настройка swagger
     *
     * @return
     */
    public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
        Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
        moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
        return moxyJsonConfig.resolver();
    }

    /**
     * Загрузка параметров из файла настроек
     *
     * @param filename
     * @return
     */
    private static boolean getPropertiesFromFile(String filename) {
        LOG.info(String.format("******************* %s **************** \n\tfilename = %s", "getPropertiesFromFile", filename));
        boolean res = false;
        try (InputStream input = new FileInputStream(filename)) {
            Properties property = new Properties();
            property.load(input);
            try {
                Object[] prop_keys = property.keySet().toArray();
                for (Object key : prop_keys) {
                    PROPERTIES.put((String) key, property.get(key));
                }
                if (Boolean.parseBoolean((String) PROPERTIES.get("app_using_env_var"))) {
                    // Получаем настройки из переменных окружения
                    Map<String, String> env = System.getenv();
                    try {
                        // Заменяем значение параметров если они переопределены через переменные окружения
                        env.forEach((t, u) -> {
                            APP_PROPERTY_LIST.forEach((t1) -> {
                                if (t.toLowerCase().matches("^" + t1.toLowerCase() + "_[a-z0-9_]{1,}$")) {
                                    PROPERTIES.put(t.toLowerCase(), u);
                                }
                            });
                        });

                    } catch (Exception e) {
                        LOG.log(Level.ERROR, e);
                    }
                }

                PROPERTIES.forEach((t, u) -> {
                    if (((String) t).contains("password")) {
                        LOG.info(String.format("%-25s = %s", t, "**************"));
                    } else {
                        LOG.info(String.format("%-25s = %s", t, (String) u));
                    }
                });
                res = true;
            } catch (Exception ex1) {
                LOG.error("Error format file properties");
                ex1.printStackTrace();
                LOG.log(Level.ERROR, ex1);
            }
        } catch (Exception ex2) {
            ex2.printStackTrace();
            LOG.log(Level.ERROR, ex2);
        }
        return res;
    }
}
