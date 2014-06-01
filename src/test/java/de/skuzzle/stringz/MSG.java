package de.skuzzle.stringz;

@ResourceMapping("de.skuzzle.stringz.test")
public class MSG {

    static {
        Stringz.init(MSG.class);
    }
    
    public static String testKey1;
    public static String testKey2;
    public static String testKey3;
    public static String testKey4;
    public static String testKey5;
    
    @NoResource
    public static String testKeyIgnore;
}
