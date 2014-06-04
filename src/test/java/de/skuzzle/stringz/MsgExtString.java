package de.skuzzle.stringz;

import de.skuzzle.stringz.annotation.FieldMapping;
import de.skuzzle.stringz.annotation.NoResource;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.extstring.ExtString;
import de.skuzzle.stringz.extstring.ExtStringFieldMapperFactory;

@ResourceMapping("de.skuzzle.stringz.test")
@FieldMapping(ExtStringFieldMapperFactory.class)
public class MsgExtString {

    static {
        Stringz.init(MsgExtString.class);
    }
    
    public static ExtString testKey1;
    public static ExtString testKey2;
    public static ExtString testKey3;
    public static ExtString testKey4;
    public static ExtString testKey5;
    
    @NoResource
    public static String testKeyIgnore;
}
