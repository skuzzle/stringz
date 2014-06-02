package de.skuzzle.stringz.strategy;

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

    public Control configureControl(ResourceControl rc, ResourceMapping mapping) 
            throws ControlConfigurationException;
    
    public FieldMapper configureFieldMapper(FieldMapping fm, ResourceMapping mapping) 
            throws FieldMappingException;
    
}
