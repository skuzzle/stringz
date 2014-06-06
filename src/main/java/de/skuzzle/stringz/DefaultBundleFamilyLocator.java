package de.skuzzle.stringz;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;

import de.skuzzle.stringz.strategy.BundleFamilyLocator;
import de.skuzzle.stringz.strategy.BundleFamilyException;

/**
 * Implements the {@link Stringz} default base name lookup strategy. It it first tries to
 * locate a public static String field with the name returned by 
 * {@link #getConstantName()}. If such a field is present, its value will be returned 
 * as base name. Otherwise, the full qualified name of the provided class itself will
 * be returned.
 * 
 * @author Simon Taddiken
 */
public class DefaultBundleFamilyLocator implements BundleFamilyLocator {
    
    /** Default name of the field which is looked up */
    protected final static String BUNDLE_FAMILY_FIELD = "BUNDLE_FAMILY";

    @Override
    public String locateBundleFamily(Class<?> messages) {
        try {
            return findNameByConstant(messages);
        } catch (BundleFamilyException e) {
            return findNameByClassName(messages);
        }
    }
    
    /**
     * Gets the constant name which is used for {@link #findNameByConstant(Class)}.
     * 
     * @return Name of the constant which holds the bundle family name.
     */
    protected String getConstantName() {
        return BUNDLE_FAMILY_FIELD;
    }
    
    /**
     * Tries to locate a public static String constant with the name provided by
     * {@link #getConstantName()}. If such field is present in the provided class,
     * its value is returned. Otherwise, a {@link BundleFamilyException} is 
     * thrown.
     *  
     * @param messages The class in which to look up the constant.
     * @return The bundle family name to use, specified as value of a constant in the
     *          provided class.
     */
    protected String findNameByConstant(Class<?> messages) {
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
    
    /**
     * Basically, returns the full qualified name of the provided class.
     * 
     * @param messages A class.
     * @return The full qualified name of that class.
     */
    protected String findNameByClassName(Class<?> messages) {
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
