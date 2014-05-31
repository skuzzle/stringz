package de.skuzzle.stringz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used to apply a custom bundle family lookup for a single {@link Messages} 
 * implementation. You have to implement the family lookup strategy using 
 * {@link BundleFamilyLocator} and supply an instance of that class to 
 * {@link Stringz#registerLocator(BundleFamilyLocator)}. Using this annotation, you
 * can instruct stringz to use your new custom locator when initializing a 
 * <tt>Messages</tt> instance:
 * 
 * <pre>
 * &#64;FamilyLocator(MyCustomFamilyLocator.class)
 * public class MSG implements Messages {
 *     //...
 * }
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FamilyLocator {
    /**
     * Defines the {@link BundleFamilyLocator} class to use when locating the bundle 
     * family for a <tt>ResourceMapping</tt> class. A concrete implementation of the class
     * specified as value has to be registered with 
     * {@link Stringz#registerLocator(BundleFamilyLocator)}.
     * 
     * @return The class of the {@link BundleFamilyLocator} to use with this class.
     */
    Class<? extends BundleFamilyLocator> value() default PublicFieldBundleFamilyLocator.class;
}
