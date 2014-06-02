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

public class CachedStrategies implements Strategies {

    private final Map<Class<? extends ControlConfigurator>, ControlConfigurator> controlCache;
    private final Map<Class<? extends FieldMapperConfigurator>, FieldMapperConfigurator> fieldMapperCache;

    public CachedStrategies() {
        this.controlCache = new HashMap<>();
        this.fieldMapperCache = new HashMap<>();
    }

    private <T> T getCached(Map<Class<? extends T>, T> cache, Class<? extends T> key)
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
