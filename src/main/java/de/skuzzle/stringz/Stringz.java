package de.skuzzle.stringz;

import java.lang.reflect.Modifier;
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

/**
 * Provides automatic mapping from {@link ResourceBundle ResourceBundles} to
 * public variable of a class based on their names. Those classes are called
 * <em>Message classes</em> and must be marked with the {@link ResourceMapping}
 * annotation.
 * 
 * <p>
 * Stringz uses default Java properties Resource Bundles in which externalized
 * Strings can be stored. The {@link #init(Class, Locale) init} method will then
 * try to map all public static String fields of a provided class to an entry of
 * a ResourceBundle which belongs to that class. This approach to access
 * externalized Strings has multiple advanteges over the standard approach using
 * <tt>bundle.getString(key)</tt>:
 * <ul>
 * <li>Overhead of writing code which uses externalized Strings is minimized.
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
 * Stringz uses the Java default mechanism for {@link ResourceBundle} lookup.
 * This involves specifying a <em>base name</em> and a locale. From that, a
 * String is constructed which is used to locate the resource to load. With
 * Stringz the task of specifying a base name to locate the resource can be
 * simplified.
 * 
 * <h2>Extended ResourceBundle Features</h2>
 * Stringz allows you to use normal <tt>properties</tt> files to define
 * externalized Strings but it offers some advanced features over normal
 * properties files.
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
 * Properties files used by the Stringz class can also have a special key
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
 * @author Simon Taddiken
 */
public final class Stringz {

    /** The default locale to use */
    private static volatile Locale locale = Locale.getDefault();

    /**
     * Registered locators to find the bundle family name for a Messages
     * implementation
     */
    private final static Map<Class<? extends BundleFamilyLocator>, BundleFamilyLocator> FAMILY_LOCATORS = new HashMap<>();

    /** Stores Control instances which can be referenced by a name */
    private final static Map<Class<? extends ControlConfigurator>, ControlConfigurator> CONTROLS = new HashMap<>();

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
     * {@link BundleFamilyLocatorException} is thrown.
     * 
     * @param cls The class of the locator to retrieve.
     * @return The locator that was registered for that class.
     * @throws BundleFamilyLocatorException If there is no locator registered
     *             for the specified class.
     */
    private static BundleFamilyLocator getLocator(
            Class<? extends BundleFamilyLocator> cls) {
        synchronized (FAMILY_LOCATORS) {
            final BundleFamilyLocator locator = FAMILY_LOCATORS.get(cls);
            if (locator == null) {
                throw new BundleFamilyLocatorException(String.format(
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

    /** Not instantiatable (is this even a word?) */
    private Stringz() {}

    /** Holds all classes which have already been initialized */
    private final static Set<Class<?>> initialized = new HashSet<>();

    /**
     * Initializes the provided <em>message class</em> using the provided
     * locale. The class must be annotated with the {@link ResourceMapping}
     * annotation. This method will look up a {@link ResourceBundle} which
     * belongs to the provided class. It will then initialize all public static
     * String variables of the provided class with values from the retrieved
     * resource bundle using the variable's name as look up key. If the bundle
     * does not contain a value for a variable, a
     * {@link java.util.MissingResourceException} will be thrown.
     * 
     * <p>
     * If the provided <tt>ResourceMapping</tt> annotation does <b>not</b>
     * provide a base name for finding the bundle (i.e.
     * {@link ResourceMapping#value()} returns the empty String), a default
     * procedure is started in order to locate the bundle which belongs to the
     * provided class:
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
     * If the {@link ResourceMapping#value() ResourceMapping} specifies any
     * other String than the empty String, that String is used as base name for
     * resource bundle location. If the ResourceBundle could not be resolved
     * using the base name, a {@link java.util.MissingResourceException} is
     * thrown.
     * </p>
     * 
     * @param cls The class to initialize.
     * @param locale The locale used to locate the proper resource bundle.
     * @throws IllegalArgumentException If either parameter is <code>null</code>
     *             or if the provided class is not annotated with
     *             {@link ResourceMapping}.
     * @throws java.util.MissingResourceException If either the
     *             {@link ResourceBundle} can not be found or the provided class
     *             contains a public static String variable for which no mapping
     *             exists in the ResourceBundle.
     * @throws BundleFamilyLocatorException If the class is annotated with
     *             {@link FamilyLocator} and the specified
     *             {@link BundleFamilyLocator} is not
     *             {@link #registerLocator(BundleFamilyLocator) registered}. Or
     *             if the used {@link BundleFamilyLocator} fails to locate the
     *             bundle family.
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
                        baseName, field.getName(), value), e);
                }
            });
    }

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
            return configureControl(rc, mapping);
        } else {
            return new CharsetBundleControl(mapping.encoding());
        }
    }

    private static Control configureControl(ResourceControl rc, ResourceMapping mapping) {
        synchronized (CONTROLS) {
            ControlConfigurator control = CONTROLS.get(rc.value());
            if (control == null) {
                try {
                    control = rc.value().newInstance();
                    CONTROLS.put(rc.value(), control);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new ControlConfigurationException(String.format(
                        "Could not create ControlConfigurator for class %s",rc.value()), 
                        e);
                }
            }
            return control.configure(mapping, rc.args());
        }
    }
}