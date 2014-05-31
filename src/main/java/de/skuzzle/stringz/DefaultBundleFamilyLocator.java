package de.skuzzle.stringz;

public class DefaultBundleFamilyLocator implements BundleFamilyLocator {
    
    private final BundleFamilyLocator byPackageLocator;
    private final BundleFamilyLocator byPublicFieldLocator;
    
    public DefaultBundleFamilyLocator() {
        this.byPackageLocator = new PackageRelativeBundleFamilyLocator();
        this.byPublicFieldLocator = new PublicFieldBundleFamilyLocator();
    }

    @Override
    public String locateBundleFamily(Class<?> messages) {
        try {
            return this.byPublicFieldLocator.locateBundleFamily(messages);
        } catch (BundleFamilyLocatorException e) {
            return this.byPackageLocator.locateBundleFamily(messages);
        }
    }
}
