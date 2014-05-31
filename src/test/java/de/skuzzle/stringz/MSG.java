package de.skuzzle.stringz;

public class MSG implements Messages {

    public final static String BUNDLE_FAMILY = "de.skuzzle.stringz.test";
    
    static {
        Stringz.init(MSG.class);
    }
    
    public static String testKey1;
    public static String testKey2;
    public static String testKey3;
    public static String testKey4;
    public static String testKey5;
}
