package de.skuzzle.stringz;

// name as test.properties in same package
public class test implements Messages {
    
    static {
        Stringz.init(test.class);
    }
    
    public static String testKey1;
    public static String testKey2;
    public static String testKey3;
    public static String testKey4;
    public static String testKey5;
}
