package de.skuzzle.stringz;

import de.skuzzle.stringz.strategy.BundleFamilyLocator;

public class StaticFamilyLocator implements BundleFamilyLocator {

    @Override
    public String locateBundleFamily(Class<?> messages) {
        return "de.skuzzle.stringz.test";
    }
}
