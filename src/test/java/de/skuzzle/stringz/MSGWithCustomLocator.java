package de.skuzzle.stringz;

import de.skuzzle.stringz.annotation.FamilyLocator;
import de.skuzzle.stringz.annotation.ResourceMapping;

@ResourceMapping
@FamilyLocator(StaticFamilyLocator.class)
public class MSGWithCustomLocator {
    
    static {
        Stringz.init(MSGWithCustomLocator.class);
    }
    
    public static String testKey1;
    public static String testKey2;
    public static String testKey3;
    public static String testKey4;
    public static String testKey5;
}
