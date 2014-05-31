package de.skuzzle.stringz;

public class StaticFamilyLocator implements BundleFamilyLocator {

    @Override
    public String locateBundleFamily(Class<?> messages) {
        return "de.skuzzle.stringz.test";
    }
}
