package de.skuzzle.stringz;

@ResourceMapping("de.skuzzle.stringz.testWithInclude1")
public class MSGWithInclude {

    static {
        Stringz.init(MSGWithInclude.class);
    }

    // included fields
    public static String testKey1;
    public static String testKey2;

    public static String testKey6;
    public static String testKey7;
}
