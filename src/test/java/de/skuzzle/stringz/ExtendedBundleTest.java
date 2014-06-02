package de.skuzzle.stringz;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.junit.Assert;
import org.junit.Test;

public class ExtendedBundleTest {

    private ResourceBundle getBundle() {
        return getBundle("test");
    }

    private ResourceBundle getBundle(String name) {
        final ClassLoader loader = getClass().getClassLoader();
        final Locale locale = Locale.ROOT;
        final Control control = new CharsetBundleControl("UTF-8");
        return ExtendedBundle.getBundle("de.skuzzle.stringz." + name, locale,
                loader, control);
    }

    private ResourceBundle getBundleWithIncludes() {
        return getBundle("testWithInclude1");
    }

    private ResourceBundle getBundleWithTransitiveIncludes() {
        return getBundle("transitiveInclude");
    }

    private ResourceBundle getBundleWithMultipleIncludes() {
        return getBundle("multipleIncludes");
    }
    
    @Test
    public void testGetSimple() {
        final ResourceBundle bundle = getBundle();
        Assert.assertEquals("Some value", bundle.getObject("testKey1"));
    }

    @Test
    public void testSimpleReplace() {
        final ResourceBundle bundle = getBundle();
        Assert.assertEquals("A String with a Reference to 'Some value'",
                bundle.getObject("testKey2"));
    }

    @Test
    public void testReferenceAtTheBeginning() {
        final ResourceBundle bundle = getBundle();
        Assert.assertEquals("Some value reference at the beginning",
                bundle.getObject("testKey4"));
    }

    @Test
    public void testReferenceAtTheEnd() {
        final ResourceBundle bundle = getBundle();
        Assert.assertEquals("reference at the end Some value",
                bundle.getObject("testKey5"));
    }

    @Test
    public void testGetNestedReplace() {
        final ResourceBundle bundle = getBundle();
        Assert.assertEquals(
                "This is a String with reference to A String with a Reference to 'Some value'",
                bundle.getObject("testKey3"));
    }

    @Test
    public void testSimpleInclude() {
        final ResourceBundle bundle = getBundleWithIncludes();
        Assert.assertEquals("Some value", bundle.getObject("testKey6"));
    }

    @Test
    public void testReferenceToIncludedValue() {
        final ResourceBundle bundle = getBundleWithIncludes();
        Assert.assertEquals("Reference to include Some value",
                bundle.getObject("testKey7"));
    }

    @Test
    public void testTransitiveIncludeReferenceToParentParent() {
        final ResourceBundle bundle = getBundleWithTransitiveIncludes();
        Assert.assertEquals("Some value reference to parent' parent",
                bundle.getObject("testKey8"));
    }
    
    @Test
    public void testWithMutlipleIncludesFirstReference() {
        final ResourceBundle bundle = getBundleWithMultipleIncludes();
        Assert.assertEquals("A reference to first include: Some value",
                bundle.getObject("testKey9"));
    }
    
    @Test
    public void testWithMutlipleIncludesSecondReference() {
        final ResourceBundle bundle = getBundleWithMultipleIncludes();
        Assert.assertEquals("A reference to second include: Some value",
                bundle.getObject("testKey10"));
    }
    
    @Test(expected = MissingResourceException.class)
    public void testUnknownKey() {
        final ResourceBundle bundle = getBundle();
        bundle.getObject("keyWhichDoesNotExist");
    }
    
    @Test(expected = MissingResourceException.class)
    public void testUnknownInclude() {
        getBundle("testWithUnknownInclude");
    }
}
