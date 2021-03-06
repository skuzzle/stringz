package de.skuzzle.stringz;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle.Control;

import de.skuzzle.stringz.annotation.FieldMapping;
import de.skuzzle.stringz.annotation.ResourceControl;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.ControlFactory;
import de.skuzzle.stringz.strategy.ControlFactoryException;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperException;
import de.skuzzle.stringz.strategy.FieldMapperFactory;
import de.skuzzle.stringz.strategy.Strategies;

/**
 * These strategies only instantiate every {@link ControlFactory} and
 * {@link FieldMapperFactory} once. When a {@code Factory} for the same class
 * is requested, the cached instance will be returned. If you require a simple,
 * non-caching implementation, use {@link SimpleStrategies} instead.
 *
 * <p>Call {@code Stringz.setStrategies(new CachedStrategies()} to use this
 * implementation.</p>
 *
 * @author Simon Taddiken
 * @see SimpleStrategies
 */
public class CachedStrategies implements Strategies {

    /** Cache for {@link ControlFactory ControlFactorys} */
    protected final Map<Class<? extends ControlFactory>, ControlFactory> controlCache;

    /** Cache for {@link FieldMapperFactory FieldMapperFactorys} */
    protected final Map<Class<? extends FieldMapperFactory>, FieldMapperFactory> fieldMapperCache;

    /**
     * Creates a new cached strategies instance.
     */
    public CachedStrategies() {
        this.controlCache = new HashMap<>();
        this.fieldMapperCache = new HashMap<>();
    }

    /**
     * This method returns an object T which is stored under the specified {@code key}
     * in the provided {@code cache}. If no instance for T is cached, a new one will be
     * instantiated and stored.
     *
     * <p>This method is synchronized on the passed {@code cache}.</p>
     *
     * @param <T> Type of values in the cache.
     * @param cache The cache to retrieve the instance from or to put a newly created
     *          instance in.
     * @param key The key of the object to retrieve.
     * @return A cached instance of the class {@code key} which is optionally created
     *          if none existed in the cache.
     * @throws InstantiationException If creation of a new instance of T failed.
     * @throws IllegalAccessException If creation of a new instance of T failed.
     */
    protected <T> T getCached(Map<Class<? extends T>, T> cache, Class<? extends T> key)
            throws InstantiationException, IllegalAccessException {
        synchronized (cache) {
            T result = cache.get(key);
            if (result == null) {
                try {
                    final Constructor<? extends T> ctor = key.getConstructor();
                    ctor.setAccessible(true);
                    result = ctor.newInstance();
                    cache.put(key, result);
                } catch (NoSuchMethodException | SecurityException
                        | IllegalArgumentException | InvocationTargetException e) {
                    throw new StringzRuntimeException(e);
                }
            }
            return result;
        }
    }

    @Override
    public Control getControl(ResourceControl rc, ResourceMapping mapping)
            throws ControlFactoryException {
        try {
            final ControlFactory control = getCached(this.controlCache,
                    rc.value());
            return control.create(mapping, rc.args());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ControlFactoryException(String.format(
                    "Could not create ControlFactory for class %s", rc.value()), e);
        }
    }

    @Override
    public FieldMapper getFieldMapper(FieldMapping fm, ResourceMapping mapping)
            throws FieldMapperException {
        try {
            final FieldMapperFactory control = getCached(this.fieldMapperCache,
                    fm.value());
            return control.create(mapping, fm.args());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FieldMapperException(String.format(
                    "Could not create FieldMapper for class %s", fm.value()), e);
        }
    }
}
