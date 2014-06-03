package de.skuzzle.stringz;

import java.util.ResourceBundle.Control;

import de.skuzzle.stringz.annotation.FieldMapping;
import de.skuzzle.stringz.annotation.ResourceControl;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.ControlConfigurationException;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMappingException;
import de.skuzzle.stringz.strategy.Strategies;

/**
 * This is the simplest possible implementation of the {@link Strategies} interface.
 * Each <tt>configureXX</tt> method will always instantiate a new instance of the 
 * required <tt>Configurator</tt> and will then use the Configurator to create the
 * desired object. If you would like to cache <tt>Configurator</tt> instances, use
 * {@link CachedStrategies} instead.
 * 
 * <p>Call <tt>Stringz.setStrategies(new CachedStrategies()</tt> to use this 
 * implementation.</p>
 * 
 * @author Simon Taddiken
 * @see SimpleStrategies
 */
public class SimpleStrategies implements Strategies {

    @Override
    public Control configureControl(ResourceControl rc, ResourceMapping mapping)
            throws ControlConfigurationException {
        try {
            return rc.value().newInstance().configure(mapping, rc.args());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ControlConfigurationException(String.format(
                    "Could not create ControlConfigurator for class %s", rc.value()), e);
        }
    }

    @Override
    public FieldMapper configureFieldMapper(FieldMapping fm, ResourceMapping mapping)
            throws FieldMappingException {
        try {
            return fm.value().newInstance().configure(mapping, fm.args());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FieldMappingException(String.format(
                    "Could not create FieldMapper for class %s", fm.value()), e);
        }
    }
}
