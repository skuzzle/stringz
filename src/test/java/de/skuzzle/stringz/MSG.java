package de.skuzzle.stringz;

import de.skuzzle.stringz.annotation.Delimiter;
import de.skuzzle.stringz.annotation.NoResource;
import de.skuzzle.stringz.annotation.ResourceCollection;
import de.skuzzle.stringz.annotation.ResourceKey;
import de.skuzzle.stringz.annotation.ResourceMapping;

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
    public static String[] arrayKey;
    
    @Delimiter(",")
    public static String[] commaDelimited;
    
    @ResourceKey("commaDelimited")
    @Delimiter(",")
    public static String[] customMapping;
    
    @ResourceCollection({"testKey1", "testKey2"})
    public static String[] customMapping2;
    
    @NoResource
    public static String testKeyIgnore;
}
