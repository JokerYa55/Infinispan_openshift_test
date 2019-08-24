/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 *
 * @author vasil
 */
public class AppConst {

    private static final Logger LOG = Logger.getLogger(AppConst.class);

    public static final String APP_PATH = System.getProperty("user.dir");
    public static final HashMap<String, Object> PROPERTIES = new HashMap<>();
    public static final List<String> APP_PROPERTY_LIST = new LinkedList(Arrays.asList("sso", "app"));
    public static final String LOG_HEADER_FORMAT_STR = "----------- %-20s -----------";
    private static Cache<String, Object> cache;
    private static final Object CACHE_LOCK = new Object();

    /**
     * Возвращает название мнтода откуда она вызвана
     *
     * @return
     */
    public static String getCurrentMethodName() {
        //return Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName();
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /**
     * Загрузка параметров из файла настроек
     *
     * @param filename
     * @return
     */
    public static boolean getPropertiesFromFile(String filename) {
        String methodName = getCurrentMethodName();
        LOG.info(String.format(LOG_HEADER_FORMAT_STR, methodName));
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

    /**
     * 
     * @return 
     */
    public static Cache<String, Object> getCache() {
        String methodName = getCurrentMethodName();
        LOG.info(String.format(LOG_HEADER_FORMAT_STR, methodName));
        synchronized (CACHE_LOCK) {
            if (cache == null) {
                // Инициализируем Cache
                GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
                global.transport().clusterName("cluster");
                DefaultCacheManager cacheManager = new DefaultCacheManager(global.build());
                Configuration config = cacheManager
                        .defineConfiguration("weather",
                                new ConfigurationBuilder()
                                        .clustering()
                                        .cacheMode(CacheMode.DIST_SYNC)
                                        .build()
                        );
                cache = cacheManager.getCache("test");

            }
        }
        return cache;
    }
}
