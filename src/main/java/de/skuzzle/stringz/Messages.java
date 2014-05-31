package de.skuzzle.stringz;

/**
 * This is mostly a tagging interface for classes which hold <tt>public static</tt> 
 * String fields. Those fields can automatically be initialized with an externalized 
 * String which is read from a resource bundle.
 * 
 * <p>Implementations are required to be initialized using the {@link Stringz} class
 * which will load the externalized strings. In order to locate the resource bundle for
 * your implementation, the following default strategy is applied. Say your 
 * <tt>Messages</tt> implementation's class name is 'MSG' and it resists within the 
 * package 'com.your.domain'.
 * <ol>
 *   <li>If your class contains a <tt>public final static String</tt> field named
 *      <tt>BUNDLE_FAMILY</tt>, the fields value will be used to locate the bundle</li>
 *   <li>Otherwise, the string <tt>com.your.domain.MSG</tt> is used as resource family 
 *      name.</li>
 * </ol> 
 * 
 * <p>If no resource bundle can be found for the provided family name, an exception will
 * be thrown upon initialization. It is possible to customize the family name lookup
 * by specifying a {@link BundleFamilyLocator} using the {@link FamilyLocator} annotation
 * on your <tt>Messages</tt> implementation.</p>
 * 
 * <p>In order to initialize your implementation so that its public fields get filled
 * with their respective Strings, you need to statically call any of the Stringz classes
 * <tt>init</tt> methods. Below is an example:</p>
 * 
 * <pre>
 * public class MSG implements Messages {
 * 
 *     // Define the family of the bundle to retrieve. In this example it is assumed
 *     // that the package com.your.domain contains a properties file named 
 *     // 'Translation' with optional locale postfix appended (as for default resource 
 *     // bundle lookup).
 *     // HINT: This constant must exactly be defined as public static String field
 *     //       with the exact same name. Additionally it must be defined before
 *     //       calling the init method (see below)
 *     
 *     public final static String BUNDLE_FAMILY = "com.your.domain.Translation";
 *     
 *     static {
 *         // Initialize all public static String fields in this class with either:
 *         Stringz.init(); // uses reflection to find the caller class
 *         // or
 *         String.init(MSG.class);
 *         // or
 *         Stringz.init(myBundleConfiguration); // See StringzConfiguration interface.
 *     }
 *     
 *     // Those fields will be automatically initialized to the strings read from the
 *     // resource bundle when the init() method is called.
 *     public static String abort;
 *     public static String finish;
 *     public static String next;
 *     public static String back;
 * }
 * </pre>
 * 
 * @author Simon Taddiken
 */
public interface Messages {

    public static String bind(String msg, Object...params) {
        return String.format(msg, params);
    }
}
