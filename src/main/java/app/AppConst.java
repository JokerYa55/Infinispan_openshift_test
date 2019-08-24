/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vasil
 */
public class AppConst {
    public static final String APP_PATH = System.getProperty("user.dir");
    public static final HashMap<String, Object> PROPERTIES = new HashMap<>();
    public static final List<String> APP_PROPERTY_LIST = new LinkedList(Arrays.asList("sso", "app"));
}
