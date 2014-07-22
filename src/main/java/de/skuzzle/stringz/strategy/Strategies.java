package de.skuzzle.stringz.strategy;

import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import de.skuzzle.stringz.CachedStrategies;
import de.skuzzle.stringz.SimpleStrategies;
import de.skuzzle.stringz.Stringz;
import de.skuzzle.stringz.annotation.FieldMapping;
import de.skuzzle.stringz.annotation.ResourceControl;
import de.skuzzle.stringz.annotation.ResourceMapping;

/**
 * Defines the way in which {@link Stringz} uses different strategies, like
 * {@link FieldMapper} and {@link Control} instances.
 *
 * @author Simon Taddiken
 * @see SimpleStrategies
 * @see CachedStrategies
 */
public interface Strategies {

    /**
     * Creates the {@link Control} instance which should be used to load the
     * {@link ResourceBundle} for the class annotated with the provided
     * {@link ResourceControl} and {@link ResourceMapping}.
     *
     * <p>
     * It is meant to obtain a {@link ControlFactory} instance using the
     * {@link Class} value of the provided {@link ResourceControl#value()
     * ResourceControl}, then use that <tt>ControlFactory's</tt>
     * {@link ControlFactory#create(ResourceMapping, String[]) create} method to
     * create a new {@link Control} instance.
     * </p>
     *
     * @param rc The <tt>ResourceControl</tt> annotation of the target message
     *            class.
     * @param mapping The <tt>ResourceMapping</tt> annotation of the target
     *            message class.
     * @return A Control instance for loading the <tt>ResourceBundle</tt> for
     *         the target message class.
     * @throws ControlFactoryException If creation of the <tt>Control</tt>
     *             instance fails for any reason.
     */
    public Control getControl(ResourceControl rc, ResourceMapping mapping)
            throws ControlFactoryException;

    /**
     * Creates the {@link FieldMapper} instance which should be used to map the
     * fields of the target message class to values from a
     * {@link ResourceBundle}.
     *
     * <p>
     * It is meant to obtain a {@link FieldMapperFactory} instance using the
     * {@link Class} value of the provided {@link FieldMapping#value()
     * FieldMapping}, then use that <tt>FieldMapperFactory</tt>
     * {@link FieldMapperFactory#create(ResourceMapping, String[]) create}
     * method to create a new {@link FieldMapper} instance.
     * </p>
     *
     * @param fm The <tt>FieldMapping</tt> annotation of the target message
     *            class.
     * @param mapping The <tt>ResourceMapping</tt> annotation of the target
     *            message class.
     * @return A <tt>FieldMapper</tt> instance which should be used to map
     *         fields of the target message class to resource values.
     * @throws FieldMapperException If creation of the <tt>FieldMapper</tt>
     *             instance fails for any reason.
     */
    public FieldMapper getFieldMapper(FieldMapping fm, ResourceMapping mapping)
            throws FieldMapperException;

}
