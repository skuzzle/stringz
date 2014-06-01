package de.skuzzle.stringz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Maps a so called <em>Message Class</em> to a {@link java.util.ResourceBundle}.
 * A Message class is a Java class that only contains 
 * <tt>public static String</tt> fields which should be mapped to a corresponding 
 * entry within a {@link java.util.ResourceBundle} using their name.</p>
 * 
 * <p>This annotation provides the information that are needed in order to look up the 
 * ResourceBundle from which the mappings are loaded. The {@link #value()} field specifies
 * the <tt>baseName</tt> in terms of {@link java.util.ResourceBundle#getBundle(String)}.
 * This property defaults to the empty String. If you do not explicitly specify a base
 * name, a custom look up procedure is applied by the {@link Stringz} class in order to
 * automatically resolve the base name.</p>
 *  
 * <p>Using {@link #encoding()} you can additionally specify the charset in which your 
 * bundle is stored. The default value is "UTF-8"</p>
 *
 * <p>A sample Message class looks like this:</p>
 * <pre>
 * package com.your.domain;
 * 
 * import de.skuzzle.stringz.Stringz;
 * 
 * &#64;ResourceMapping
 * public class MSG {
 *     static {
 *         Stringz.init(MSG.class);
 *     }
 *     
 *     public static String next;
 *     public static String back;
 *     public static String cancel;
 * }
 * </pre>
 * 
 * <p>In this example, there is no base name specified within the {@link ResourceMapping}
 * annotation. In this case, the Stringz class first looks for a public static String 
 * constant called <em>BUNDLE_FAMILY</em>. If this constant is not present, it will use
 * the name of the class prepended with the class's package name as base name. So in the 
 * above sample, the base name would resolve to <em>com.your.domain.MSG</em>.</p>
 * 
 * <p>You may also explicitly specify the base name to use:</p>
 * <pre>
 * package com.your.domain;
 * 
 * import de.skuzzle.stringz.Stringz;
 * 
 * &#64;ResourceMapping("com.your.domain.MSG")
 * public class MSG {
 *     // stays the same
 * }
 * </pre>
 * 
 * If you require crazy custom strategies, you could use a {@link BundleFamilyLocator}:
 * <pre>
 * package com.your.domain;
 * 
 * import de.skuzzle.stringz.FamilyLocator;
 * import de.skuzzle.stringz.Stringz;
 * import de.skuzzle.stringz.ResourceMapping;
 * 
 * &#64;ResourceMapping
 * &#64;FamilyLocator(MyCustomFamilyLocator.class)
 * public class MSG {
 *     // stays the same
 * }
 * </pre>
 * 
 * After calling {@link Stringz#init(Class)}, all public and static String fields will
 * be initialized with a corresponding String from the ResourceBundle.
 * 
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceMapping {
    /**
     * Specifies the resource bundle's family name for lookup by the
     * {@link java.util.ResourceBundle} class. If left empty (default value),
     * custom family lookup is applied by the {@link Stringz} class.
     * 
     * @return The base name for {@link java.util.ResourceBundle} lookup or the empty
     *          String. Defaults to the empty String.
     */
    public String value() default "";

    /**
     * Defines the encoding in which the resource bundle for this mapped
     * resources is stored. The default value is "UTF-8".
     * 
     * @return The encoding in which the mapped {@link java.util.ResourceBundle}
     *         is stored.
     */
    public String encoding() default "UTF-8";
    
    /**
     * Instructs the Stringz class to {@link String#intern()} all Strings loaded from
     * the provided ResourceBundle. Defaults to <code>false</code>.
     * @return Whether to <em>intern</em> Strings from this bundle. 
     */
    public boolean intern() default false;
}
