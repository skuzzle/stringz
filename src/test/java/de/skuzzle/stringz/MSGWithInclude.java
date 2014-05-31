package de.skuzzle.stringz;

public class MSGWithInclude implements Messages {

    public final static String BUNDLE_FAMILY = "de.skuzzle.stringz.testWithInclude1";

    static {
        Stringz.init(MSGWithInclude.class);
    }

    // included fields
    public static String testKey1;
    public static String testKey2;

    public static String testKey6;
    public static String testKey7;
}
