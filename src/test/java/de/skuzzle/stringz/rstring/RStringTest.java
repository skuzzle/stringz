package de.skuzzle.stringz.rstring;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import de.skuzzle.stringz.Stringz;

public class RStringTest {

    @Test
    public void testExtString() {
        Stringz.setLocale(Locale.ROOT);
        Assert.assertEquals("Some value", MsgRString.testKey1.s);
        Assert.assertEquals("A String with a Reference to 'Some value'",
                MsgRString.testKey2.s);
        Assert.assertEquals(
                "This is a String with reference to A String with a Reference to 'Some value'",
                MsgRString.testKey3.s);
        Assert.assertEquals("Some value reference at the beginning",
                MsgRString.testKey4.s);
        Assert.assertEquals("reference at the end Some value", MsgRString.testKey5.s);
    }

}
