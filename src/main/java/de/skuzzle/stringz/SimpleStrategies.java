package de.skuzzle.stringz;

import java.util.ResourceBundle.Control;

import de.skuzzle.stringz.annotation.FieldMapping;
import de.skuzzle.stringz.annotation.ResourceControl;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.ControlFactoryException;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperException;
import de.skuzzle.stringz.strategy.Strategies;

/**
 * This is the simplest possible implementation of the {@link Strategies}
 * interface. Each getter method will always instantiate a new instance of the
 * required {@code Factory} and will then use the Factory to create the desired
 * object. If you would like to cache {@code Factory} instances, use
 * {@link CachedStrategies} instead.
 *
 * <p>
 * Call {@code Stringz.setStrategies(new CachedStrategies())} to use this
 * implementation.
 * </p>
 *
 * @author Simon Taddiken
 * @see CachedStrategies
 */
public class SimpleStrategies implements Strategies {

    @Override
    public Control getControl(ResourceControl rc, ResourceMapping mapping)
            throws ControlFactoryException {
        try {
            return rc.value().newInstance().create(mapping, rc.args());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ControlFactoryException(String.format(
                    "Could not create ControlFactory for class %s", rc.value()), e);
        }
    }

    @Override
    public FieldMapper getFieldMapper(FieldMapping fm, ResourceMapping mapping)
            throws FieldMapperException {
        try {
            return fm.value().newInstance().create(mapping, fm.args());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new FieldMapperException(String.format(
                    "Could not create FieldMapper for class %s", fm.value()), e);
        }
    }
}
