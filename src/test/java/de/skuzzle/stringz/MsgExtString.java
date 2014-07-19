package de.skuzzle.stringz;

import de.skuzzle.stringz.annotation.FieldMapping;
import de.skuzzle.stringz.annotation.NoResource;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.rstring.RString;
import de.skuzzle.stringz.rstring.RStringFieldMapperFactory;

@ResourceMapping("de.skuzzle.stringz.test")
@FieldMapping(RStringFieldMapperFactory.class)
public class MsgExtString {

    static {
        Stringz.init(MsgExtString.class);
    }
    
    public static RString testKey1;
    public static RString testKey2;
    public static RString testKey3;
    public static RString testKey4;
    public static RString testKey5;
    
    @NoResource
    public static String testKeyIgnore;
}
