package de.skuzzle.stringz;

import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperFactory;
import de.skuzzle.stringz.strategy.FieldMappingException;

/**
 * Factory implementation which creates {@link DefaultFieldMapper} instances.
 * 
 * @author Simon Taddiken
 */
public class DefaultFieldMapperFactory implements FieldMapperFactory {

    /**
     * {@inheritDoc}
     * 
     * @return A FieldMapper which implements the Stringz default field mapping behavior.
     * @throws FieldMappingException If the <tt>args</tt> array is not empty.
     */
    @Override
    public FieldMapper create(ResourceMapping mapping, String[] args)
            throws FieldMappingException {
        if (args.length != 0) {
            throw new FieldMappingException(
                    String.format("This factory has no arguments"));
        }
        return new DefaultFieldMapper();
    }

}
