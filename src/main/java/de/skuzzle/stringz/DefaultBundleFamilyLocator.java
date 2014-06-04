package de.skuzzle.stringz;

import de.skuzzle.stringz.strategy.BundleFamilyLocator;
import de.skuzzle.stringz.strategy.BundleFamilyException;

/**
 * Implements the {@link Stringz} default base name lookup strategy. It uses a chain of
 * {@link PackageRelativeBundleFamilyLocator} and {@link PublicFieldBundleFamilyLocator}
 * to find the base name.
 * 
 * @author Simon Taddiken
 */
public class DefaultBundleFamilyLocator implements BundleFamilyLocator {
    
    /** Locates the family by package- and class name */
    protected final BundleFamilyLocator byPackageLocator;
    
    /** Locates the family by a public static String field called BUNDLE_FAMILY */
    protected final BundleFamilyLocator byPublicFieldLocator;
    
    /**
     * Creates a new DefaultBundleFamilyLocator.
     */
    public DefaultBundleFamilyLocator() {
        this.byPackageLocator = new PackageRelativeBundleFamilyLocator();
        this.byPublicFieldLocator = new PublicFieldBundleFamilyLocator();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>First tries to locate the field name using a 
     * {@link PublicFieldBundleFamilyLocator}. If that fails, the name returned by a
     * {@link PackageRelativeBundleFamilyLocator} will be returned.
     */
    @Override
    public String locateBundleFamily(Class<?> messages) {
        try {
            return this.byPublicFieldLocator.locateBundleFamily(messages);
        } catch (BundleFamilyException e) {
            return this.byPackageLocator.locateBundleFamily(messages);
        }
    }
}
