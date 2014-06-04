package de.skuzzle.stringz;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;

import de.skuzzle.stringz.strategy.BundleFamilyLocator;
import de.skuzzle.stringz.strategy.BundleFamilyException;

public class PublicFieldBundleFamilyLocator implements BundleFamilyLocator {

    /** Default name of the field which is looked up */
    protected final static String BUNDLE_FAMILY_FIELD = "BUNDLE_FAMILY";
    
    protected String getConstantName() {
        return BUNDLE_FAMILY_FIELD;
    }

    @Override
    public String locateBundleFamily(Class<?> messages) {
        if (messages == null) {
            throw new IllegalArgumentException("messages is null");
        }
        final String constant = getConstantName();
        final Optional<Field> f = Arrays.stream(messages.getFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()) &&
                        Modifier.isPublic(field.getModifiers()) &&
                        Modifier.isFinal(field.getModifiers()) &&
                        field.getType() == String.class &&
                        field.getName().equals(constant))
                .findFirst();
        if (!f.isPresent()) {
            throw new BundleFamilyException(String.format(
                    "Class %s has no public static field with name %s",
                    messages.getName(), constant));
        }
        try {
            return (String) f.get().get(null);
        } catch (IllegalAccessException e) {
            throw new BundleFamilyException(
                    String.format("Field %s of class %s inaccessible",
                            constant,
                            messages.getName()));
        }
    }
}
