package de.skuzzle.stringz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.Set;

import de.skuzzle.stringz.annotation.Delimiter;
import de.skuzzle.stringz.annotation.FamilyLocator;
import de.skuzzle.stringz.annotation.FieldMapping;
import de.skuzzle.stringz.annotation.NoResource;
import de.skuzzle.stringz.annotation.ResourceCollection;
import de.skuzzle.stringz.annotation.ResourceControl;
import de.skuzzle.stringz.annotation.ResourceKey;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.BundleFamilyLocator;
import de.skuzzle.stringz.strategy.BundleFamilyException;
import de.skuzzle.stringz.strategy.ControlFactoryException;
import de.skuzzle.stringz.strategy.ControlFactory;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperException;
import de.skuzzle.stringz.strategy.FieldMapperFactory;
import de.skuzzle.stringz.strategy.Strategies;

/**
 * Provides automatic mapping from {@link ResourceBundle ResourceBundles} to
 * public variables of a class based on their names. Those classes are called
 * <em>Message classes</em> and must be marked with the {@link ResourceMapping}
 * annotation. Before using stringz, you should be aware of how ResourceBundles work
 * (<a href="http://docs.oracle.com/javase/tutorial/i18n/resbundle/index.html">See the 
 * Oracle Online Trail about Resource Bundles</a>).
 * 
 * <p>
 * Stringz uses default Java properties Resource Bundles in which externalized
 * Strings can be stored. The {@link #init(Class, Locale) init} method will then
 * try to map all public static String fields of a provided class to an entry of
 * a ResourceBundle which belongs to that class. A simple message class looks like this:
 * </p>
 * 
 * <pre>
 * &#64;ResourceMapping
 * public class MSG {
 *     static {
 *         Stringz.init(MSG.class);
 *     }
 *     
 *     public static String resourceKey;
 *     public static String resourceKey1;
 *     public static String resourceKeyX;
 *     // ...
 * }
 * </pre>
 * 
 * <p>This approach to access externalized Strings has multiple advantages over the 
 * standard approach using <tt>bundle.getString("resourceKey")</tt>:
 * <ul>
 *   <li>Overhead of writing code which uses externalized Strings is minimized.
 * Instead of having to write code like
 * 
 * <pre>
 * final ResourceBundle bundle = ResourceBundle.getBundle(...);
 * final String applicationName = bundle.get("applicationName");
 * </pre>
 * 
 * with stringz, you could simply write
 * 
 * <pre>
 * final String applicationName = MSG.applicationName;
 * </pre>
 * 
 * </li>
 * <li>With Stringz, the memory overhead of storing the {@link Map} which holds
 * the properties file's entries can be avoided. After a <em>message class</em>
 * has been initialized, the {@link ResourceBundle} instance itself can be
 * disposed as it is no longer used. Memory consumption is reduced to the plain
 * value needed to store the single Strings, without the Map overhead</li>
 * <li>Externalized Strings are accessed using real Java variables instead of
 * String literals with <tt>bundle.getString("key");</tt></li>
 * </ul>
 * 
 * <h2>ResourceBundle Lookup</h2>
 * <p>Stringz provides two stages of customizing the resource bundle look up process.
 * The first stage is about resolving the base name of a resource bundle which should be 
 * mapped to a message class. To simplify the specification of the base name, the default
 * behavior is to use the full qualified name of the message class itself.
 * If your message class is <tt>com.you.domain.MSG</tt>, Stringz will use exactly that 
 * String as base name to find <tt>MSG.properties, MSG_En.properties</tt>, etc. within 
 * the same package of that class.</p>
 * 
 * <p>If this behavior does not suit your needs, there are several ways of customizing
 * the base name used for a message class. First, there are two ways two explicitly
 * specify a base name:</p>
 * <ol>
 *   <li>Define the field <tt>public final static String BUNDLE_FAMILY</tt> in your 
 *       message class. Then, the value of that field will be used as base name.
 *   </li>
 *   <li>The preferred way is to specify the base name directly within the 
 *       {@link ResourceMapping} annotation (e.g. 
 *       <tt>&#64;ResourceMapping("com.your.domain.BaseName")</tt>)
 *   </li>
 * </ol>
 * <p>Furthermore, you may {@link #registerLocator(BundleFamilyLocator) register} a 
 * {@link BundleFamilyLocator} locator with the <tt>Stringz</tt> class and specify the 
 * locator to use on your message class using the {@link FamilyLocator} annotation. Here 
 * is a sample <tt>BundleFamilyLocator</tt>:</p>
 * 
 * <pre>
 * public class StaticFamilyLocator implements BundleFamilyLocator {
 *     &#64;Override
 *     public String locateBundleFamily(Class&lt;?&gt; msg) {
 *         return "com.your.domain.SomeBaseName";
 *     }
 * }
 * </pre>
 * 
 * Register the locator with the <tt>Stringz</tt> class:
 * 
 * <pre>
 * Stringz.registerLocator(new StaticFamilyLocator());
 * </pre>
 * 
 * Specify the locator to use on your message class:
 * <pre>
 * &#64;ResourceMapping()   // important: leave value() attribute empty
 * &#64;FamilyLocator(StaticFamilyLocator.class)
 * public class MSG {
 *     static {
 *         Stringz.init(MSG.class);
 *     }
 *     // ..
 * }
 * </pre>
 * 
 * <p>The second stage of bundle look up customization is about the 
 * {@link java.util.ResourceBundle.Control Control} instance which is used to create the
 * ResourceBundle for your message class. By default, Stringz uses a <tt>Control</tt> 
 * implementation which uses the {@link ResourceMapping#encoding() charset} specified in 
 * the ResourceMapping and creates a {@link java.util.PropertyResourceBundle}. If you 
 * want to supply a custom <tt>Control</tt> implementation, you can mark your message 
 * class with the {@link ResourceControl}. This annotation specifies a 
 * {@link ControlFactory} class which can be used to create a <tt>Control</tt> 
 * instance which suits your needs. Below is an example usage. First, create your 
 * <tt>ControlFactory</tt> class:</p>
 * <pre>
 * public class MyControlFactory implements ControlFactory {
 *     &#64;Override
 *     public Control create(ResourceMapping mapping, String[] args) {
 *         final String encoding = mapping.encoding();
 *         return new YourControlImplementation(encoding);
 *     }
 * }
 * </pre>
 * Now, specify that this factory should be used to read the <tt>ResourceBundle</tt>
 * for your message class:
 * <pre>
 * &#64;ResourceMapping(value = "com.your.domain.BaseName", encoding = "iso-8859")
 * &#64;ResourceControl(MyControlFactory.class)
 * public class MSG {
 *     static {
 *         Stringz.init(MSG.class);
 *     }
 *     // ..
 * }
 * </pre>
 * Stringz will create an instance of <tt>MyControlFactory</tt> and will then call
 * its {@link ControlFactory#create(ResourceMapping, String[]) configure} method
 * to obtain a <tt>Control</tt> instance.
 * 
 * <h2>Field Mapping</h2>
 * <p>By default, {@link Stringz} iterates all public static String fields of a class and 
 * tries to assign each variable the value from the resource bundle stored with the name
 * of that variable. If you want a variable to be mapped to an explicitly different 
 * resource, you can specify an alternate key to use for look up with 
 * {@link ResourceKey} annotation. If you do not want a field to be mapped to a resource
 * value, you can mark it with {@link NoResource}.</p>
 * 
 * <p>Besides simple Strings, String arrays are also supported. If Stringz finds a public
 * static String[] variable, it tries to split the respective resource value using a
 * {@link DefaultFieldMapper#getDefaultDelimiter() default} delimiter. You can also 
 * specify an explicit delimiter pattern using {@link Delimiter} annotation. Finally, you
 * can construct array resources from multiple other resources using 
 * {@link ResourceCollection}:</p>
 * <pre>
 * &#64;ResourceCollection({"keyOfOtherResource", "keyOfSecondOtherResource"})
 * public static String[] customArray;
 * </pre>
 * <p>In this case, the array's name will be ignored for resource look up but an array 
 * from the specified resource names will be created.</p>
 * 
 * <p>The whole field mapping process can be customized in the same way in which you can
 * customize the resource look up process as described above. Here you would need 
 * a {@link FieldMapper} and a {@link FieldMapperFactory} to supply custom behavior. The
 * default implementation can be found in {@link DefaultFieldMapper}.</p>
 * 
 * <h2>Extended ResourceBundle Features</h2>
 * Stringz allows you to use normal <tt>property</tt> files to define
 * externalized Strings but it offers some advanced features over normal
 * java property files.
 * 
 * <h3>Key References</h3>
 * Within a mapping pair, you can refer to any other key within the scope of
 * your properties file. For example:
 * 
 * <pre>
 * userName = Username
 * promptUserName = Please insert your ${userName}
 * </pre>
 * 
 * Accessing the resource with name <tt>promptUserName</tt> will yield the
 * String <tt>Please insert your Username</tt>. These key references are also
 * applied transitively:
 * 
 * <pre>
 * userName = Username
 * promptUserName = Please insert your ${userName}
 * promptPassword = ${promptUserName} and password
 * </pre>
 * 
 * Here, the resource with name <tt>promptPassword</tt> would resolve to the
 * String <tt>Please insert your Username and password</tt>.
 * 
 * <h3>Inclusion of Other Bundles</h3>
 * Properties files used by the <tt>Stringz</tt> class can also have a special key
 * mapping named <tt>&#64;include</tt> which expects a semicolon separated list
 * of other ResourceBundle baseNames to include into the scope of the current
 * file. This will make all mappings of the included bundles visible for key
 * references as described above.
 * 
 * <pre>
 * &#64;include = com.your.domain.CommonNames;
 * key = ${referenceToKeyFromIncludedBundle}
 * </pre>
 * 
 * Included key/value pairs will also be mapped to the String variables of your message
 * class.
 * 
 * @author Simon Taddiken
 */
public final class Stringz {
    
    /**
     * Registered locators to find the bundle family name for a Messages
     * implementation
     */
    private final static Map<Class<? extends BundleFamilyLocator>, BundleFamilyLocator> 
        FAMILY_LOCATORS = new HashMap<>();

    /** Holds all classes which have already been initialized */
    private final static Set<Class<?>> initialized = new HashSet<>();
    
    /** 
     * The default FieldMapper for classes which have ne {@link FieldMapping} annotation
     */
    private final static FieldMapper DEFAULT_FIELD_MAPPER = new DefaultFieldMapper();

    /**
     * The default Strategy.
     */
    private final static Strategies DEFAULT_STRATEGIES = new CachedStrategies();
    

    /** The default locale to use */
    private static volatile Locale locale = Locale.getDefault();

    /** Currently used strategies */
    private static volatile Strategies strategies = DEFAULT_STRATEGIES;
    
    /**
     * Registers the provided {@link BundleFamilyLocator}. The provided instance
     * will be registered under the class which is returned by its
     * <tt>getClass()</tt> method. It can be referenced by that class within the
     * {@link FamilyLocator} annotation.
     * 
     * @param locator The locator to register.
     */
    public static void registerLocator(BundleFamilyLocator locator) {
        synchronized (FAMILY_LOCATORS) {
            FAMILY_LOCATORS.put(locator.getClass(), locator);
        }
    }

    /**
     * Retrieves a {@link BundleFamilyLocator} instance for the provided class.
     * If no locator has been registered for the provided class, a
     * {@link BundleFamilyException} is thrown.
     * 
     * @param cls The class of the locator to retrieve.
     * @return The locator that was registered for that class.
     * @throws BundleFamilyException If there is no locator registered
     *             for the specified class.
     */
    private static BundleFamilyLocator getLocator(
            Class<? extends BundleFamilyLocator> cls) {
        synchronized (FAMILY_LOCATORS) {
            final BundleFamilyLocator locator = FAMILY_LOCATORS.get(cls);
            if (locator == null) {
                throw new BundleFamilyException(String.format(
                        "No BundleFamilyLocator registered for '%s'", cls.getName()));
            }
            return locator;
        }
    }

    static {
        registerLocator(new DefaultBundleFamilyLocator());
    }

    /**
     * (Re)Configures Stringz to use the provided <tt>locale</tt>. If the new
     * locale is different from the current (as determined by
     * {@link Locale#equals(Object)}, all public message fields of already
     * initialized <em>Message classes</em> will be reinitialized using the new
     * locale.
     * 
     * @param locale The locale to use when locating resource bundles. If
     *            <code>null</code>, {@link Locale#getDefault()} is used.
     */
    public static void setLocale(Locale locale) {
        locale = locale == null ? Locale.getDefault() : locale;
        final boolean eq = locale.equals(Stringz.locale);
        Stringz.locale = locale;
        if (!eq) {
            // reinitialize all previously loaded classes
            synchronized (initialized) {
                final Collection<Class<?>> copy = new ArrayList<>(initialized);
                initialized.clear();
                copy.forEach(cls -> init(cls, Stringz.locale));
            }
        }
    }
    
    /**
     * Sets the {@link Strategies} to use.
     * @param strategies The strategies to use. If <code>null</code>, a default 
     *          implementation will be used.
     */
    public static void setStrategies(Strategies strategies) {
        synchronized (DEFAULT_STRATEGIES) {
            strategies = strategies == null ? DEFAULT_STRATEGIES : strategies;
            Stringz.strategies = strategies;
        }
    }

    /** Not instantiatable (is this even a word?) */
    private Stringz() {}

    /**
     * Initializes the provided <em>message class</em> using the provided
     * locale. The class must be annotated with the {@link ResourceMapping}
     * annotation. This method will look up a {@link ResourceBundle} which
     * belongs to the provided class. It will then initialize all public static
     * String variables of the provided class with values from the retrieved
     * resource bundle using the variable's name as look up key. If the bundle
     * does not contain a value for a variable, a
     * {@link java.util.MissingResourceException} will be thrown (except if that field
     * is marked as {@link NoResource}).
     * 
     * <p>
     * If the provided <tt>ResourceMapping</tt> annotation does <b>not</b>
     * provide a base name for finding the bundle (i.e.
     * {@link ResourceMapping#value()} returns the empty String), a default
     * procedure is started in order to locate the bundle which belongs to the
     * provided class:</p>
     * <ol>
     * <li>If the provided class is annotated with {@link BundleFamilyLocator}
     * then the provided locator is used to find the base name for the bundle.</li>
     * <li>Else, if the provided class contains the field
     * <tt>public static String BUNDLE_FAMILY</tt>, its value will be used as
     * base name.</li>
     * <li>Otherwise, the base name is constructed using the name of the package
     * of the provided class appended with the class's name. That is, the
     * resource is expected to be in the same package and have the same base
     * name as the class is named.</li>
     * </ol>
     * 
     * <p>If the {@link ResourceMapping#value() ResourceMapping} specifies any
     * other String than the empty String, that String is used as base name for
     * resource bundle location. If the ResourceBundle could not be resolved
     * using the base name, a {@link java.util.MissingResourceException} is
     * thrown.</p>
     * 
     * <p>If the provided class is annotated with {@link ResourceControl}, the therein
     * specified {@link ControlFactory} class will be used to create a 
     * {@link java.util.ResourceBundle.Control} instance which in turn is used to resolve 
     * the ResourceBundle from which the Strings are loaded. If no such annotation is 
     * present, the Java default behavior is used.</p>
     * 
     * @param cls The class to initialize.
     * @param locale The locale used to locate the proper resource bundle.
     * @throws IllegalArgumentException If either parameter is <code>null</code>
     *             or if the provided class is not annotated with {@link ResourceMapping}.
     * @throws java.util.MissingResourceException If either the
     *             {@link ResourceBundle} can not be found or the provided class
     *             contains a public static String variable for which no mapping
     *             exists in the ResourceBundle.
     * @throws BundleFamilyException If the class is annotated with
     *             <tt>&#64;FamilyLocator</tt> and the specified 
     *             <tt>BundleFamilyLocator</tt> is not
     *             {@link #registerLocator(BundleFamilyLocator) registered}. Or if the 
     *             used <tt>BundleFamilyLocator</tt> fails to locate the bundle family.
     * @throws ControlFactoryException If the class is annotated with 
     *             <tt>&#64;ResourceControl</tt> and the provided 
     *             <tt>ControlFactory</tt> class could not be instantiated or if its 
     *             {@link ControlFactory#create(ResourceMapping, String[]) configure} 
     *             method failed to create a <tt>Control</tt> instance.
     * @throws FieldMapperException If the used {@link FieldMapper} failed to assign a 
     *             value to a field.             
     */
    public static void init(Class<?> cls, Locale locale) {
        if (cls == null) {
            throw new IllegalArgumentException("cls is null");
        } else if (locale == null) {
            throw new IllegalArgumentException("locale is null");
        } else if (!cls.isAnnotationPresent(ResourceMapping.class)) {
            throw new IllegalArgumentException(String.format(
                    "Class %s does not specify a ResourceMapping annotation", cls));
        }

        synchronized (initialized) {
            if (!initialized.add(cls)) {
                // we already handled this
                return;
            }
        }

        final ResourceMapping rm = cls.getAnnotation(ResourceMapping.class);
        final String baseName = findBaseName(cls, rm);

        // Retrieve the bundle. Will throw an exception if it could not be
        // found.
        final Control control = findControl(cls, rm);
        final ResourceBundle bundle = ExtendedBundle.getBundle(baseName,
                locale, cls.getClassLoader(), control);

        final FieldMapper fieldMapper = findFieldMapper(cls, rm);
        // Map fields to bundle entries
        Arrays.stream(cls.getFields())
            .filter(fieldMapper::accept)
            .forEach(field -> fieldMapper.mapField(rm, field, bundle));
    }

    /**
     * This method calls {@link #init(Class, Locale)} with the locale set by 
     * {@link #setLocale(Locale)} as parameter. Please refer to this method for full
     * documentation of how message classes are initialized.
     * 
     * @param cls The class to initialize.
     */
    public static void init(Class<?> cls) {
        init(cls, Stringz.locale);
    }

    /**
     * Finds the base name for a {@link ResourceBundle} given a class which is annotated
     * with {@link ResourceMapping}. If the annotation specifies a non-empty base name,
     * that name is returned. Otherwise, if the class is annotated with 
     * {@link FamilyLocator}, a {@link BundleFamilyLocator} is looked up and used to 
     * find the base name. If no FamilyLocator is specified, it is checked whether the
     * class contains a <tt>public final static String BUNDLE_FAMILY</tt> field. If so,
     * the value of that field will be used as base name. If no such field is given,
     * the full qualified name of the passed class is used.
     * 
     * @param cls The message class to find the base name for.
     * @param rm The ResourceMapping annotation of that class.
     * @return A base name for {@link ResourceBundle} look up.
     */
    private static String findBaseName(Class<?> cls, ResourceMapping rm) {
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
        return baseName;
    }

    private static Control findControl(Class<?> cls, ResourceMapping mapping) {
        if (cls.isAnnotationPresent(ResourceControl.class)) {
            final ResourceControl rc = cls.getAnnotation(ResourceControl.class);
            return strategies.getControl(rc, mapping);
        } else {
            return new CharsetBundleControl(mapping.encoding());
        }
    }
    
    private static FieldMapper findFieldMapper(Class<?> cls, ResourceMapping mapping) {
        if (cls.isAnnotationPresent(FieldMapping.class)) {
            final FieldMapping fm = cls.getAnnotation(FieldMapping.class);
            return strategies.getFieldMapper(fm, mapping);
        } else {
            return DEFAULT_FIELD_MAPPER;
        }
    }
}