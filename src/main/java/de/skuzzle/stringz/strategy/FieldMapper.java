package de.skuzzle.stringz.strategy;

import java.lang.reflect.Field;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.skuzzle.stringz.Stringz;
import de.skuzzle.stringz.annotation.ResourceMapping;

/**
 * A {@code FieldMapper} can be used to customize the way in which fields of a class
 * are mapped to values from a {@link ResourceBundle}. The {@link #accept(Field)} method
 * is used to select the fields which should get a value assigned using
 * {@link #mapField(ResourceMapping, Field, ResourceBundle)}.
 *
 * @author Simon Taddiken
 */
public interface FieldMapper {

    /**
     * Determines whether this field should be mapped to a resource value. All
     * fields passed to this method by the {@link Stringz} class will be static
     * ones.
     *
     * @param field The field to check.
     * @return Whether this field should be mapped to a value from a
     *         {@link ResourceBundle}.
     */
    public boolean accept(Field field);

    /**
     * Sets the value of the provided field to a value from the {@link ResourceBundle}.
     * The passed field is one which has previously been accepted by
     * {@link #accept(Field)}.
     *
     * @param mapping The {@link ResourceMapping} annotation of the message class which
     *          is currently processed.
     * @param field The field which value should be set.
     * @param bundle The {@code ResourceBundle} which belongs to the message class which
     *          is currently processed.
     * @throws FieldMapperException If assigning the desired value to {@code field}
     *          fails.
     * @throws MissingResourceException If a resource is accessed using a name which does
     *          not exist in the {@code bundle}.
     */
    public void mapField(ResourceMapping mapping, Field field, ResourceBundle bundle)
            throws FieldMapperException, MissingResourceException;
}
