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

public final class Stringz {

    /**
     * The default configuration for retrieving bundles. This uses the system's
     * default {@link Locale} and UTF-8 as bundle encoding.
     */
    private final static StringzConfiguration DEFAULT_CONFIGURATION =
            new StringzConfiguration() {
                @Override
                public Locale getLocale() {
                    return Locale.getDefault();
                }

                @Override
                public String getBundleCharsetName() {
                    return "UTF-8";
                }
            };

    /** The configuration to use when using init method with no config parameter */
    private static StringzConfiguration configuration = DEFAULT_CONFIGURATION;

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
     * (Re)Configures the Stringz default configuration to use the provided
     * <tt>configuration</tt>. If the new configuration is different from the
     * current (as determined by {@link StringzConfiguration#equals(Object)},
     * all public message fields of already initialized message classes will be
     * reinitialized using the new locale and encoding.
     * 
     * @param configuration The configuration to use when locating and reading
     *            resource bundles. If <code>null</code>,
     *            {@link #DEFAULT_CONFIGURATION} is used.
     */
    public static void configure(StringzConfiguration configuration) {
        configuration = configuration == null ? DEFAULT_CONFIGURATION
                : configuration;
        synchronized (DEFAULT_CONFIGURATION) {
            final boolean eq = configuration.equals(Stringz.configuration);
            Stringz.configuration = configuration;
            if (!eq) {
                // reinitialize all previously loaded classes
                synchronized (initialized) {
                    initialized.forEach(cls -> init(cls, Stringz.configuration));
                }
            }
        }
    }

    /** Not instantiatable (is this even a word?) */
    private Stringz() {}

    private final static Set<Class<?>> initialized = new HashSet<>();

    public static void init(Class<?> cls, StringzConfiguration config) {
        if (cls == null) {
            throw new IllegalArgumentException("cls is null");
        } else if (config == null) {
            throw new IllegalArgumentException("config is null");
        } else if (!Messages.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Class %s does not implement the interface 'Messages'",
                            cls));
        }
        synchronized (initialized) {
            if (!initialized.add(cls)) {
                // we already handled this
                return;
            }
        }

        final Locale locale = config.getLocale();
        final String charsetName = config.getBundleCharsetName();
        final Control control = new CharsetBundleControl(charsetName);

        final Class<? extends BundleFamilyLocator> locatorClass;
        if (cls.isAnnotationPresent(FamilyLocator.class)) {
            final FamilyLocator fl = cls.getAnnotation(FamilyLocator.class);
            locatorClass = fl.value();
        } else {
            locatorClass = DefaultBundleFamilyLocator.class;
        }
        
        final BundleFamilyLocator familyLocator = getLocator(locatorClass);
        final String baseName = familyLocator.locateBundleFamily(cls);
        final ResourceBundle bundle = ExtendedBundle.getBundle(baseName,
                locale,
                cls.getClassLoader(), control);

        Arrays.stream(cls.getFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()) &&
                        Modifier.isPublic(field.getModifiers()) &&
                        !Modifier.isFinal(field.getModifiers()) &&
                        field.getType() == String.class)
                .forEach(
                        field -> {
                            field.setAccessible(true);
                            final String value = bundle.getString(field
                                    .getName());
                            try {
                                field.set(null, value);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(
                                        String.format(
                                                "Resource initialization failed. family=%s, field=%s, value=%s",
                                                baseName, field.getName(),
                                                value), e);
                            }
                        });
    }

    public static void init(Class<?> cls) {
        synchronized (Stringz.configuration) {
            init(cls, Stringz.configuration);
        }
    }
}