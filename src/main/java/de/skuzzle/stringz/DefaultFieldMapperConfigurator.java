package de.skuzzle.stringz;

import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperConfigurator;
import de.skuzzle.stringz.strategy.FieldMappingException;

public class DefaultFieldMapperConfigurator implements FieldMapperConfigurator {

    @Override
    public FieldMapper configure(ResourceMapping mapping, String[] args)
            throws FieldMappingException {
        if (args.length != 0) {
            throw new FieldMappingException(
                    String.format("This configurator has no arguments"));
        }
        return new DefaultFieldMapper();
    }

}
