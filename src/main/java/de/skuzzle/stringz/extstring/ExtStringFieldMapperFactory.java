package de.skuzzle.stringz.extstring;

import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperFactory;
import de.skuzzle.stringz.strategy.FieldMappingException;

public class ExtStringFieldMapperFactory implements FieldMapperFactory {

    @Override
    public FieldMapper create(ResourceMapping mapping, String[] args)
            throws FieldMappingException {
        
        if (args.length != 0) {
            throw new FieldMappingException("No arguments expected");
        }
        
        return new ExtStringFieldMapper();
    }
}