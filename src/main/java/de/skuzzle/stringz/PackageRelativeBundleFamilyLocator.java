package de.skuzzle.stringz;

import de.skuzzle.stringz.strategy.BundleFamilyLocator;
import de.skuzzle.stringz.strategy.BundleFamilyException;


public class PackageRelativeBundleFamilyLocator implements BundleFamilyLocator {

    @Override
    public String locateBundleFamily(Class<?> messages) {
        final Package pack = messages.getPackage();
        final String cname = messages.getSimpleName();
        if (cname.isEmpty()) {
            throw new BundleFamilyException(String.format(
                    "Class %s seems to be anonymous", messages.getName()));
        }
        final String family = String.format("%s.%s", pack.getName(), cname); 
        return family;
    }

}
