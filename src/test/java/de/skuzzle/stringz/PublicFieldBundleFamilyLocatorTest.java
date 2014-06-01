package de.skuzzle.stringz;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PublicFieldBundleFamilyLocatorTest {

    private BundleFamilyLocator subject;

    @Before
    public void setup() {
        subject = new PublicFieldBundleFamilyLocator();
    }
    
    @Test
    public void testBasenameLookUp() {
        final String baseName = subject.locateBundleFamily(MSGWithFieldBaseName.class);
        Assert.assertEquals("de.skuzzle.stringz.test", baseName);
    }
    
    @Test(expected = BundleFamilyLocatorException.class)
    public void testBasenameLookUpFaile() {
        final String baseName = subject.locateBundleFamily(MSG.class);
        Assert.assertEquals("de.skuzzle.stringz.test", baseName);
    }
}
