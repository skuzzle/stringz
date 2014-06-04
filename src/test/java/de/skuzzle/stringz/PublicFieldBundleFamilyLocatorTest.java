package de.skuzzle.stringz;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.skuzzle.stringz.strategy.BundleFamilyLocator;
import de.skuzzle.stringz.strategy.BundleFamilyException;

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
    
    @Test(expected = BundleFamilyException.class)
    public void testBasenameLookUpFaile() {
        final String baseName = subject.locateBundleFamily(MSG.class);
        Assert.assertEquals("de.skuzzle.stringz.test", baseName);
    }
}
