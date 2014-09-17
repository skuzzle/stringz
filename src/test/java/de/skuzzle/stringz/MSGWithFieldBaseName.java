package de.skuzzle.stringz;

import de.skuzzle.stringz.annotation.ResourceMapping;

@ResourceMapping
public class MSGWithFieldBaseName {
    public static final String BUNDLE_FAMILY = "de.skuzzle.stringz.test";
    
    static {
        Stringz.init(MSGWithFieldBaseName.class);
    }
    
    public static String testKey1;
    public static String testKey2;
    public static String testKey3;
    public static String testKey4;
    public static String testKey5;
}
