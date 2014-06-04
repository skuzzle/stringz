package de.skuzzle.stringz.strategy;

import java.util.ResourceBundle;

import de.skuzzle.stringz.Stringz;
import de.skuzzle.stringz.annotation.FieldMapping;
import de.skuzzle.stringz.annotation.ResourceMapping;

/**
 * Factory interface for {@link FieldMapper} instances. Implementations of this 
 * interface are required to have a no arguments constructor if they are to be used with
 * the {@link Stringz} class.
 * 
 * <p>On a class marked with {@link ResourceMapping} you can additionally specify the
 * {@link FieldMapper} which serves for chosing which fields of the annotated class
 * get mapped to what value from a {@link ResourceBundle}. You can specify the 
 * {@link FieldMapperFactory} class using the {@link FieldMapping} annotation.
 * {@link Stringz} will then try to instantiate the provided class and call its 
 * {@link #create(ResourceMapping, String[])} method in order to retrieve a 
 * {@link FieldMapper} instance.</p>
 * 
 * @author Simon Taddiken
 */
public interface FieldMapperFactory {

    /**
     * Creates a {@link FieldMapper} instance which will be used to map fields of a class
     * to resource values from a {@link ResourceBundle}.
     * 
     * @param mapping The ResourceMapping annotation of the message class
     * @param args Additional arguments from the {@link FieldMapping#args()} attribute.
     * @return The FieldMapper instance for this message class.
     * @throws FieldMapperException If invalid <tt>args</tt> have been provided.
     */
    public FieldMapper create(ResourceMapping mapping, String[] args) 
            throws FieldMapperException;
}