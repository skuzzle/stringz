package de.skuzzle.stringz;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle.Control;

import de.skuzzle.stringz.annotation.FieldMapping;
import de.skuzzle.stringz.annotation.ResourceControl;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.ControlConfigurationException;
import de.skuzzle.stringz.strategy.ControlConfigurator;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperConfigurator;
import de.skuzzle.stringz.strategy.FieldMappingException;
import de.skuzzle.stringz.strategy.Strategies;

/**
 * These strategies only instantiate every {@link ControlConfigurator} and 
 * {@link FieldMapperConfigurator} once. When a <tt>Configurator</tt> for the same class 
 * is requested, the cached instance will be returned. If you require a simple, 
 * non-caching implementation, use {@link SimpleStrategies} instead.
 * 
 * <p>Call <tt>Stringz.setStrategies(new CachedStrategies()</tt> to use this 
 * implementation.</p>
 * 
 * @author Simon Taddiken
 * @see SimpleStrategies
 */
public class CachedStrategies implements Strategies {

    /** Cache for {@link ControlConfigurator ControlConfigurators} */
    protected final Map<Class<? extends ControlConfigurator>, ControlConfigurator> controlCache;
    
    /** Cache for {@link FieldMapperConfigurator FieldMapperConfigurators} */
    protected final Map<Class<? extends FieldMapperConfigurator>, FieldMapperConfigurator> fieldMapperCache;

    /**
     * Creates a new cached strategies instance.
     */
    public CachedStrategies() {
        this.controlCache = new HashMap<>();
        this.fieldMapperCache = new HashMap<>();
    }

    /**
     * This method returns an object T which is stored under the specified <tt>key</tt>
     * in the provided <tt>cache</tt>. If no instance for T is cached, a new one will be
     * instantiated and stored.
     * 
     * <p>This method is synchronized on the passe <tt>cache</tt>.</p> 
     * 
     * @param <T> Type of values in the cache.
     * @param cache The cache to retrieve the instance from or to put a newly created 
     *          instance in.
     * @param key The key of the object to retrieve.
     * @return A cached instance of the class <tt>key</tt> which is optionally created
     *          if none existed in the cache.
     * @throws InstantiationException If creation of a new instance of T failed.
     * @throws IllegalAccessException If creation of a new instance of T failed.
     */
    protected <T> T getCached(Map<Class<? extends T>, T> cache, Class<? extends T> key)
            throws InstantiationException, IllegalAccessException {
        synchronized (cache) {
            T result = cache.get(key);
            if (result == null) {
                result = key.newInstance();
                cache.put(key, result);
            }
            return result;
        }
    }

    @Override
    public Control configureControl(ResourceControl rc, ResourceMapping mapping)
            throws ControlConfigurationException {
        try {
            final ControlConfigurator control = getCached(this.controlCache,
                    rc.value());
            return control.configure(mapping, rc.args());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ControlConfigurationException(String.format(
                    "Could not create ControlConfigurator for class %s", rc.value()), e);
        }
    }

    @Override
    public FieldMapper configureFieldMapper(FieldMapping fm, ResourceMapping mapping)
            throws FieldMappingException {
        try {
            final FieldMapperConfigurator control = getCached(this.fieldMapperCache,
                    fm.value());
            return control.configure(mapping, fm.args());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FieldMappingException(String.format(
                    "Could not create FieldMapper for class %s", fm.value()), e);
        }
    }
}
