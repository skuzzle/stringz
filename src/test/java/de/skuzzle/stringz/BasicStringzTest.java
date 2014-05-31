package de.skuzzle.stringz;


import org.junit.Assert;
import org.junit.Test;

public class BasicStringzTest {

    @Test
    public void testSimpleMsg() {
        Assert.assertEquals("Some value", MSG.testKey1);
        Assert.assertEquals("A String with a Reference to 'Some value'", MSG.testKey2);
        Assert.assertEquals("This is a String with reference to A String with a Reference to 'Some value'", MSG.testKey3);
        Assert.assertEquals("Some value reference at the beginning", MSG.testKey4);
        Assert.assertEquals("reference at the end Some value", MSG.testKey5);
    }
    
    @Test
    public void testMsgWithIncludedFields() {
        Assert.assertEquals("Some value", MSGWithInclude.testKey1);
        Assert.assertEquals("A String with a Reference to 'Some value'", MSGWithInclude.testKey2);
        Assert.assertEquals("Some value", MSGWithInclude.testKey6);
        Assert.assertEquals("Reference to include Some value", MSGWithInclude.testKey7);
    }
    
    @Test
    public void testBundleFamilyLocator() {
        Assert.assertEquals("Some value", test.testKey1);
        Assert.assertEquals("A String with a Reference to 'Some value'", test.testKey2);
        Assert.assertEquals("This is a String with reference to A String with a Reference to 'Some value'", test.testKey3);
        Assert.assertEquals("Some value reference at the beginning", test.testKey4);
        Assert.assertEquals("reference at the end Some value", test.testKey5);
    }
    
    @Test
    public void testCustomFamilyLocator() {
        Stringz.registerLocator(new StaticFamilyLocator());
        Assert.assertEquals("Some value", MSGWithCustomLocator.testKey1);
        Assert.assertEquals("A String with a Reference to 'Some value'", MSGWithCustomLocator.testKey2);
        Assert.assertEquals("This is a String with reference to A String with a Reference to 'Some value'", MSGWithCustomLocator.testKey3);
        Assert.assertEquals("Some value reference at the beginning", MSGWithCustomLocator.testKey4);
        Assert.assertEquals("reference at the end Some value", MSGWithCustomLocator.testKey5);
    }
}
