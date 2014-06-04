package de.skuzzle.stringz.extstring;

import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperFactory;
import de.skuzzle.stringz.strategy.FieldMapperException;

public class ExtStringFieldMapperFactory implements FieldMapperFactory {

    @Override
    public FieldMapper create(ResourceMapping mapping, String[] args)
            throws FieldMapperException {
        
        if (args.length != 0) {
            throw new FieldMapperException("No arguments expected");
        }
        
        return new ExtStringFieldMapper();
    }
}