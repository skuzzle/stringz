package de.skuzzle.stringz;

@FamilyLocator(StaticFamilyLocator.class)
public class MSGWithCustomLocator implements Messages {
    
    static {
        Stringz.init(MSGWithCustomLocator.class);
    }
    
    public static String testKey1;
    public static String testKey2;
    public static String testKey3;
    public static String testKey4;
    public static String testKey5;
}
