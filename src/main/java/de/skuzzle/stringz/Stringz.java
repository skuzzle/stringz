package de.skuzzle.stringz;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.Set;

/**
 * Provides automatic mapping from {@link ResourceBundle ResourceBundles} to public 
 * variable of a class based on their names. Those classes are called 
 * <em>Message classes</em> and must be marked with the {@link ResourceMapping}
 * annotation.
 * 
 * <p>Stringz uses default Java properties Resource Bundles in which externalized Strings
 * can be stored. The {@link #init(Class, Locale) init} method will then try to map
 * all public static String fields of a provided class to an entry of a ResourceBundle
 * which belongs to that class. This approach to access externalized Strings has multiple
 * advanteges over the standard approach using <tt>bundle.getString(key)</tt>:
 * <ul>
 *   <li>Overhead of writing code which uses externalized Strings is minimized. Instead
 *      of having to write code like
 * <pre>
 * final ResourceBundle bundle = ResourceBundle.getBundle(...);
 * final String applicationName = bundle.get("applicationName");
 * </pre>
 *      with stringz, you could simply write
 * <pre>final String applicationName = MSG.applicationName;</pre>
 *   </li>
 *   <li>With Stringz, the memory overhead of storing the {@link Map} which holds the
 *      properties file's entries can be avoided. After a <em>message class</em> has been
 *      initialized, the {@link ResourceBundle} instance itself can be disposed as it is 
 *      no longer used. Memory consumption is reduced to the plain value needed to store
 *      the single Strings, without the Map overhead
 *   </li>
 *   <li>Externalized Strings are accessed using real Java variables instead of String 
 *      literals with <tt>bundle.getString("key");</tt>
 *   </li> 
 * </ul>
 * 
 * <h2>ResourceBundle Lookup</h2>
 * 
 * <h2>Extended ResourceBundle Features</h2>
 * Stringz allows you to use normal <tt>properties</tt> files to define externalized
 * Strings but it offers some advanced features over normal properties files.
 * 
 * <h3>Key References</h3>
 * Within a mapping pair, you can refer to any other key within the scope of your 
 * properties file. For example:
 * <pre>
 * userName = Username
 * promptUserName = Please insert your ${userName}
 * </pre>
 * 
 * Accessing the resource with name <tt>promptUserName</tt> will yield the String
 * <tt>Please insert your Username</tt> These key references are also applied 
 * transitively:
 * <pre>
 * userName = Username
 * promptUserName = Please insert your ${userName}
 * promptPassword = ${promptUserName} and password
 * </pre>
 * 
 * Here, the resource with name <tt>promptPassword</tt> would resolve to the String 
 * <tt>Please insert your Username and password</tt>.
 * 
 * <h3>Inclusion of Other Bundles</h3>
 * Properties files used by the Stringz class can also have a special key mapping
 * named <tt>&#64;include</tt> which expects a semicolon separated list of other 
 * ResourceBundle baseNames to include into the scope of the current file. This will make
 * all mappings of the included bundles visible for key references as described above.
 * 
 * <pre>
 * &#64;include = com.your.domain.CommonNames;
 * key = ${referenceToKeyFromIncludedBundle}
 * </pre>
 * 
 * @author Simon Taddiken
 */
public final class Stringz {

    /** The default locale to use */
    private static volatile Locale locale = Locale.getDefault();

    /**
     * Registered locators to find the bundle family name for a Messages
     * implementation
     */
    private static Map<Class<? extends BundleFamilyLocator>, BundleFamilyLocator> 
        familyLocators = new HashMap<>();
    
    
    public static void registerLocator(BundleFamilyLocator locator) {
        synchronized (familyLocators) {
            familyLocators.put(locator.getClass(), locator);
        }
    }
    
    static {
        registerLocator(new DefaultBundleFamilyLocator());
    }
    
    
    private static BundleFamilyLocator getLocator(
            Class<? extends BundleFamilyLocator> cls) {
        synchronized (familyLocators) {
            final BundleFamilyLocator locator = familyLocators.get(cls);
            if (locator == null) {
                throw new BundleFamilyLocatorException(String.format(
                        "No BundleFamilyLocator registered for '%s'", cls.getName()));
            }
            return locator;
        }
    }
    
    

    /**
     * (Re)Configures Stringz to use the provided <tt>locale</tt>. If the new 
     * locale is different from the current (as determined by 
     * {@link Locale#equals(Object)}, all public message fields of already initialized 
     * <em>Message classes</em> will be reinitialized using the new locale and encoding.
     * 
     * @param locale The locale to use when locating and reading
     *            resource bundles. If <code>null</code>,
     *            {@link Locale#getDefault()} is used.
     */
    public static void configure(Locale locale) {
        locale = locale == null ? Locale.getDefault() : locale;
        final boolean eq = locale.equals(Stringz.locale);
        Stringz.locale = locale;
        if (!eq) {
            // reinitialize all previously loaded classes
            synchronized (initialized) {
                initialized.forEach(cls -> init(cls, Stringz.locale));
            }
        }
    }

    /** Not instantiatable (is this even a word?) */
    private Stringz() {}

    private final static Set<Class<?>> initialized = new HashSet<>();

    public static void init(Class<?> cls, Locale locale) {
        if (cls == null) {
            throw new IllegalArgumentException("cls is null");
        } else if (locale == null) {
            throw new IllegalArgumentException("locale is null");
        } else if (!cls.isAnnotationPresent(ResourceMapping.class)) {
            throw new IllegalArgumentException(String.format(
                    "Class %s does not specify a ResourceMapping", cls));
        }
        
        synchronized (initialized) {
            if (!initialized.add(cls)) {
                // we already handled this
                return;
            }
        }

        final ResourceMapping rm = cls.getAnnotation(ResourceMapping.class);
        final String charsetName = rm.encoding();
        
        final String baseName;
        if (rm.value().isEmpty()) {
            // if no family is specified, apply default family lookup procedure
            final Class<? extends BundleFamilyLocator> locatorClass;
            if (cls.isAnnotationPresent(FamilyLocator.class)) {
                final FamilyLocator fl = cls.getAnnotation(FamilyLocator.class);
                locatorClass = fl.value();
            } else {
                locatorClass = DefaultBundleFamilyLocator.class;
            }
            
            final BundleFamilyLocator familyLocator = getLocator(locatorClass);
            baseName = familyLocator.locateBundleFamily(cls);
        } else {
            baseName = rm.value();
        }
        
        // Retrieve the bundle. Will throw an exception if it could not be found.
        final Control control = new CharsetBundleControl(charsetName);
        final ResourceBundle bundle = ExtendedBundle.getBundle(baseName,
                locale, cls.getClassLoader(), control);

        // Map fields to bundle entries
        Arrays.stream(cls.getFields())
            .filter(field -> Modifier.isStatic(field.getModifiers()) &&
                    Modifier.isPublic(field.getModifiers()) &&
                    !Modifier.isFinal(field.getModifiers()) &&
                    field.getType() == String.class)
            .forEach(field -> {
                field.setAccessible(true);
                String value = bundle.getString(field.getName());
                if (rm.intern()) {
                    value = value.intern();
                }
                try {
                    field.set(null, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(String.format(
                        "Resource initialization failed. family=%s, field=%s, value=%s",
                        baseName, field.getName(),
                        value), e);
                }
            });
    }

    public static void init(Class<?> cls) {
        init(cls, Stringz.locale);
    }
}