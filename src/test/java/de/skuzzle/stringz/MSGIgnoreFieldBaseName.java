package de.skuzzle.stringz;

import de.skuzzle.stringz.annotation.ResourceMapping;

@ResourceMapping("de.skuzzle.stringz.test")
public class MSGIgnoreFieldBaseName {
    
    // should be ignored
    public static final String BUNDLE_FAMILY = "de.skuzzle.stringz.test2";
    
    static {
        Stringz.init(MSGIgnoreFieldBaseName.class);
    }
    
    public static String testKey1;
    public static String testKey2;
    public static String testKey3;
    public static String testKey4;
    public static String testKey5;
}
